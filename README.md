# Cloud-Native Reactive System (Demo Project)

I started this project in August 2024, so it's still a work in progress. However, what's been committed so far is a functioning system that you can already start playing with.


> FYI: Links provided in this document open in the same browser window. Use `CTRL` + `click` to open in new tab.

> Legend:
> * PoC = Proof of Concept
> * MVP = Minimum Viable Product

## What Does This System Do

In essence, it's a **quiz game** where you're asked random questions about countries, cities, and continents, with 4 options to choose from for each question.

For every correct answer, you earn a score. If you answer incorrectly, you're shown the correct answer along with a motivational message to keep practicing. You can also view a history of your last 5 attempts and see whether you got them right or wrong.

Other players can join in too, and there's a leaderboard that displays the top players and their scores.

## Focus Point

The focus is on building a cloud-native [Reactive System](https://www.reactivemanifesto.org/). For some of its constituent components, I am employing reactive programming model as well. In the best-case scenario—all of them.

**Frontend**

I'm primarily a backend software engineer, so frontend is there only for demo purposes. I use JQuery and AJAX calls, and they are not the point of interest, but they get the work done. 

**Security (Authentication)**

Security isn't the primary focus of this system either, but it's definitely implied (e.g., `Spring Security`, `JSON Web Tokens`, etc.).

However, depending on the architecture, not all services need to be secured in a traditional manner. For instance, once a request is within a private subnet, it can travel over HTTP instead of HTTPS (which also boosts performance). By using `JWT`, we can avoid duplicating authentication logic across each microservice, and concentrate on core business logic. The only requirement is to validate the JWT in each service.

## Architecture

The following describes architecture so far (that is, it will be expanded and updated when functionality gets added).

First of all, the system is split into 6 microservices:
- Frontend
- Gateway / Reverse-Proxy
- Quiz Service (main "brains")
- Gamification Service
- Message Broker
- Database

This fulfills the requirement of having a `Microservices` based architecture, which is like a pre-condition for a reactive system.

All microservices are wrapped into Docker containers. So, orchestration (`Service Registry`, `Service Discovery`, `Load Balancing` and cross-service `Resilience`) are provided implicitly by the use of `Docker Swarm` or `Kubernetes`, as well as a `Message Broker` (e.g. load balancing between subscribers). In other words, these functionalities are provided out-of-the box and there's no need to reimplement them using `Spring Cloud *`. In specific cases, some patterns are used explicitly, like `Circuit Breaker`.

Being wrapped into docker containers and described as Helm charts for Kubernetes, the application meets the criteria for a `cloud-native` and also `distributed` architecture. Additionally, since Kubernetes is vendor-agnostic itself, then the application is cloud-provider independent as well. My goal is to design software this way as much as possible to minimize the risk of vendor lock-in.

Static files are being served by a dedicated frontend `Nginx` server.

All requests for dynamic content get forwarded to a `Spring Cloud Gateway` server which acts as a reverse proxy for concrete microservices. That is, frontend knows only 1 host, it isn't aware of the internal distribution of endpoints. 

All logic for generating quiz questions, tracking user history and submitting answers is consolidated in a `Quiz` microservice.

Correct attempt events are published to a `RabbitMQ Message Broker` server over `AMPQ` protocol from the `Quiz` microservice. This fulfills the requirement of `Message-Driven` property of a reactive system.

These attempts are then picked up from the message queue by a `Gamification` microservice, which is responsible for storing all user scores, as well as a separate `leaderboard` db entity which stores calculated totals for each player.

That way the frontend has 3 "widgets":

1. The main quiz widget
1. History widget
1. Leaderboard widget

The important thing to highlight here is that the first 2 widgets are provided by the `Quiz` microservice, while the 3rd—by the `Gamification` microservice. If a `Gamification` service goes down, the system as a whole continues to function (you can play), but without the Leaderboard feature (so you won't see top player list). When `Gamification` gets recovered, it will catch up with messages from the message queue and leaderboard widget will reappear.

Similar approach happens with a history widget. If its code is broken, then you will still be able to play the game, but won't see the history of your last attempts.

This demonstrates and fulfills the requirement of `Resilience` (`High-Availability, HA`) of a reactive system. Additionally, behind the scenes, each widget maps to a separate REST endpoint. This is an intentional design choice that contributes to high availability. If a single HTTP request were used for all three widgets, and even one of them became unavailable, the entire request might return no data. The same issue would also occur in a server-side rendered UI. By leveraging SPA approach and decoupling the widgets into individual endpoints, we ensure better fault tolerance and resiliency. An added positive side effect is that data is served faster, as each endpoint returns only its relevant data, avoiding the delay of waiting for a single, larger response. Additionally, modern browsers can execute multiple HTTP requests in parallel (up to ~6 concurrent requests per domain), further improving performance. 

`Gateway`, `Quiz` and `Gamification` are stateless microservices, so they can be replicated as much as needed. This fulfills the requirement of `Elastic` property of a reactive system.

For Java microservices I've written a custom health check app, that pings `/actuator/health` endpoint, parses it, and specifically looks for `status` `UP`. So not only this endpoint has to respond with a `HTTP 2xx`, but also report that it's `UP`. This dedicated app is then used by the orchestration infrastructure, such as `Docker Swarm` or `Kubernetes`, to remove unhealthy nodes, and then replace them with healthy ones.

The use of node replication, dedicated Nginx server for static files in the closest proximity, caching, circuit breakers, health-checks, non-blocking I/O technologies such as `Netty`, `Java's Virtual Threads`, `Spring Reactor`, `R2DBC` fulfill the requirement of `Responsive` property of a reactive system.

1 instance of the RabbitMQ.

1 instance of Postgres database with 2 logical databases:

* quiz
* gamification

So, each microservice uses its own database, but which are hosted on the same physical database server.

All of this is designed with commodity hardware in mind, making it cost-effective and horizontally scalable on cloud platforms to ensure consistently high performance.

## Starting the project

### Step 1. Prepare the Environment

Install [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Docker Engine is enough too) on your local machine.

### Step 1.1 (Optional): Enable and Prepare Kubernetes

In `Docker Desktop` go to `Settings` → `Kubernetes` → `Enable Kubernetes`.

**Install `metrics-server` for HPA (Horizontal Pod Autoscaler):**

```shell
$ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml && kubectl patch deployment metrics-server -n kube-system --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value":"--kubelet-insecure-tls"}]'
```

**Install Ingress Controller**

```shell
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
```

**Install Helm**

SNAP:
```shell
$ sudo snap install helm --classic
```
Other options: https://helm.sh/docs/intro/install/

**Create Namespace To Be Used By the App**

```shell
$ kubectl create namespace micros
```
### Step 1.2. For Windows Users Only

You'll also need to have WSL2 (Windows Subsystem for Linux) enabled. If you don't already have it, Docker Desktop will enable it for you during the installation process.

> *Note 1: Docker can technically run on Hyper-V as well, but it's recommended to use WSL2 instead. If you get a prompt asking which one you'd like to use, choose WSL2—it's more efficient for your battery, CPU, and other resources.*

> *Note 2: You may also need to enable BIOS extensions for virtualization. Look for options like VTX, Intel Virtualization Extensions, or VT Extensions, and make sure they're enabled.*  

**Install additional software**

After installing Docker Desktop for Windows I recommend installing [Ubuntu for WSL2](https://www.microsoft.com/en-us/p/ubuntu/9pdxgncfsczv?rtc=1&activetab=pivot:overviewtab) from a Windows Store.

Next, to be able to use Ubuntu's shell I recommend installing [Windows Terminal](https://www.microsoft.com/en-us/p/windows-terminal/9n0dx20hk701#activetab=pivot:overviewtab).

**Bind Docker with your Linux distro**

Once Ubuntu app is installed, start Docker Desktop. From there go to:

1. Start Docker Desktop → 
1. Go to Settings →
1. Resources →
1. WSL Integration →
1. check "Enable integration with my default WSL distro" →
1. enable Ubuntu as you distro

From this point onwards you can interact with Docker engine through your Ubuntu distro:

1. Type `Terminal` in the Windows taskbar search window →
1. In the Terminal's top tab bar, click the drop-down arrow to open a new tab, then select "Ubuntu".

### Step 2. Get the Project

You have 2 options.

**Option 1: Clone the project (requires Git to be installed)**

Using SSH:

```shell
$ git clone git@github.com:oleg-astakhov/microservices-demo.git
```

... or using HTTPS:

```shell
$ git clone https://github.com/oleg-astakhov/microservices-demo.git
```

**Option 2: Download the project as ZIP file**

1. Navigate to [Project's GitHub homepage](https://github.com/oleg-astakhov/microservices-demo) →
1. Click green `<> Code` button
1. `Local` tab →
1. Click `Download ZIP`
1. Unzip the file to any location on your hard drive 

### Step 3. Run the Project 

**Option 1: using pre-built Docker images and Docker Compose (faster boot time)**

1. Navigate to the root of the project
1. Run
 
```shell
$ ./up-prod-compose.sh
```

**Option 2: build everything locally using Docker Compose (slower boot time)**

1. Navigate to the root of the project
1. Run

```shell
$ ./up-dev-compose.sh
```
*Note: you don't need to have Java installed because the source code is built inside the containers.*

**Option 3: Using Kubernetes Helm charts**

1. This option assumes you have enabled Kubernetes mode as describes in Step 1.1.
1. Navigate to the `<root>/kubernetes`
1. Run

```shell
$ helm install database ./prod-with-helm/database -n micros
$ helm install message-broker ./prod-with-helm/message-broker -n micros
$ helm install quiz ./prod-with-helm/quiz -n micros
$ helm install gamification ./prod-with-helm/gamification -n micros
$ helm install gateway ./prod-with-helm/gateway -n micros
$ helm install frontend ./prod-with-helm/frontend -n micros
```

### Step 4. Play With the Project

Open your web browser and navigate to http://localhost/ . 

Please allow some time for all containers to start. If you see the error message `Sorry, quiz is currently unavailable`, it likely means the containers are still starting up. Click the `Try Again` button to retry.

Additionally, if you use Docker Compose option to run the application, you can verify the status of services by running:

```bash
$ docker container ls
```
Look for `STATUS` column. You should see status `UP` as well as `(healthy)`, which means that services are ready to respond to requests.

If you started the application using Kubernetes Helm charts then run:
```shell
$ kubectl get pods -n micros
```
Look for column `READY`. All services must state `1/1`.

Once the services are up and running, you'll be presented with a Quiz game. Just follow the on-screen instructions.

## Technical Stack

* Docker
* Helm charts for Kubernetes
* Gradle
* Git
* Spock with Groovy
* Java 22 with Virtual Threads
* Spring Reactor
* Spring WebFlux
* Spring Boot 3
* Spring Boot Actuator
* Netty (for Java services)
* RabbitMQ 3 (over AMPQ)
* Nginx (for static content) 
* REST (endpoints for frontend)
* Spring Cloud Gateway (for reverse proxy)
* Resilience4j (circuit breaker)
* Spring Data JPA
* Hibernate
* Postgres 16
* R2DBC for non-blocking I/O (as PoC for a fully reactive service)
* Liquibase (for db versioning)
  
More will be added as I develop this system further. 

## Tests
All tests are written using Spock and Groovy.

You can find BDD-style end-to-end tests under: `<project root>/end-to-end-tests`

For fast access to an example class you can jump straight into  `SubmitQuizAnswerNormalFlowSpec`. 

When I write such tests I usually follow these 3 rules/guidelines:

* Tests should be highly readable (technical stuff is hidden in utils)
* Test should fit onto one screen without scrolling (this also means they are focused, I'd rather create multiple smaller tests, than 1 huge hard-to-maintain test. Exception to this is some regression type of tests)
* Tests should not depend on one another, i.e. complete isolation (end goal = parallel execution)

The last point from the above list requires careful planning and execution from the very beginning, as well as maintaining that diligence going forward. This affects both the tests and the code that is being tested. The ultimate goal is to enable **all tests to run in parallel**, which is how these tests are already designed. Running tests in parallel uses all available hardware resource efficiently and reduces the overall execution time **dramatically**.

### Running the tests

Running tests requires the Java to be installed on your machine.

1. Navigate to `<project root>`
1. Run script:
```shell
$ ./up-e2e-test-compose.sh
```
This will start all docker containers for end-to-end tests. Please wait while the entire system is ready before running the tests. To check if the system is ready you can use this command:

```shell
$ docker container ls
```
The `STATUS` column for all services must read `(healthy)`.

3. Navigate to `<project root>/end-to-end-tests`
3. Run script (Windows):
```shell
$ parallel-test.bat
```
5. Run script (Linux):
```shell
$ ./parallel-test.sh
```
Note that the tests will run in parallel on a multicore system.

**CI/CD**

Alternatively, there's a script which is designed to be used in a `CI/CD` environment, which will start Docker Compose, run then tests, and then stop Docker Compose.

Windows:
```shell
$ parallel-test-ci.bat
```
Linux:
```shell
$ ./parallel-test-ci.sh
```

`./parallel-test-ci.sh` is in fact being used by the CI workflow on GitHub. See `<root>/.github/workflows/end-to-end-tests.yml`.


### Unit tests

For fast access to code examples you can jump straight into `Quiz` microservice and look for classes:

- `QuestionsCommonServiceImplSpec`
- `GetNextQuestionServiceImplSpec`

found under:

`<projectRoot>\quiz\src\test\groovy\com\olegastakhov\microservices\quiz\<examples>`

## TODO

As of 03.09.2024 I am planning on implementing quite of few of the patterns/functionality, such as:
* `Spring Cloud Sleuth` tracing with `Zipkin`
* `Prometheus` metrics with `Grafana`
* `Consul KV` for remote configs
* `Server-Sent Events (SSE)` as PoC for pushing live data
* `Graylog` for centralized logs
* `Redis` for distributed cache / maybe locks
* `IaC` configuration files (e.g. for `eksctl`)

Maybe: 

* `MongoDB` as PoC for some functionality that I've yet to imagine
* `WebSockets over STOMP` as PoC for bidirectional high-performance communication
* `JSON Web Tokens` for authentication / session validation
