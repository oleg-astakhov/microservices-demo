# Cloud-Native Reactive System (Demo Project)

I started this project in August 2024, so it's still a work in progress. However, what's been committed so far is a functioning system that you can already start playing with.


> FYI: Links provided in this document open in the same browser window. Use `CTRL` + `click` to open in new tab.

## What Does This System Do

In essence, it's a **quiz game** where you're asked random questions about countries, cities, and continents, with 4 options to choose from for each question.

For every correct answer, you earn a score. If you answer incorrectly, you're shown the correct answer along with a motivational message to keep practicing. You can also view a history of your last 5 attempts and see whether you got them right or wrong.

Other players can join in too, and there's a leaderboard that displays the top players and their scores.

## Focus Point

I'm primarily a backend software engineer, so frontend is there only for demo purposes. I use JQuery and AJAX calls, and they are not the point of interest, but they get the work done.

The focus is on building a [Reactive System](https://www.reactivemanifesto.org/). For some of its constituent components, I am employing reactive programming model as well. In the best-case scenario—all of them.

## Architecture

The following describes architecture so far (that is, it will be expanded and updated when functionality gets added).

First of all, everything is inside Docker containers. So, orchestration (`Service Registry`, `Service Discovery`, `Load Balancing` and cross-service `Resilience`) are provided by the use of `Docker Swarm` or `Kubernetes`, as well as a `Message Broker` (e.g. load balancing between subscribers). In other words, these functionalities are provided out-of-the box and there's no need to reimplement them using `Spring Cloud *`. In specific cases, some patterns are used explicitly, like `Circuit Breaker`.

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

This demonstrates and fulfills the requirement of `Resilience` (`High-Availability, HA`) of a reactive system.

`Gateway`, `Quiz` and `Gamification` are stateless microservices, so they can be replicated as much as needed. This fulfills the requirement of `Elastic` property of a reactive system.

For Java microservices I've written a custom health check app, that pings `/actuator/health` endpoint, parses it, and specifically looks for `status` `UP`. So not only this endpoint has to respond with a `HTTP 2xx`, but also report that it's `UP`. This dedicated app is then used by the orchestration infrastructure, such as `Docker Swarm`, to remove unhealthy nodes, and then replace them with healthy ones.

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

### Step 2. For Windows Users Only

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

### Step 3. Get the Project

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

### Step 4. Run the Project 

**Option 1: using pre-built Docker images (faster boot time)**

1. Navigate to the root of the project
1. Run
 
```shell
$ ./up-prod-compose.sh
```

**Option 2: build everything locally (slower boot time)**

1. Navigate to the root of the project
1. Run

```shell
$ ./up-dev-compose.sh
```
*Note: you don't need to have Java installed because the source code is built inside the containers.*

### Step 5. Play With the Project

Open your web browser and navigate to http://localhost/ . 

Please allow some time for all containers to start. If you see the error message `Sorry, quiz is currently unavailable`, it likely means the containers are still starting up. Click the `Try Again` button to retry.

Additionally, you can verify the status of services by running:

```bash
$ docker container ls
```
Look for `STATUS` column. You should see status `UP` as well as `(healthy)`, which means that services are ready to respond to requests.

Once the services are up and running, you'll be presented with a Quiz game. Just follow the on-screen instructions.

## Technical Stack

> Legend:   
> * PoC = Proof of Concept  
> * MVP = Minimum Viable Product

* Docker
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

## TODO

As of 27.08.2024 I am planning on implementing quite of few of the patterns/functionality, such as:
* `Spring Cloud Sleuth` tracing with `Zipkin`
* `Prometheus` metrics with `Grafana`
* `Consul KV` for remote configs
* `Server-Sent Events (SSE)` as PoC for pushing live data
* `Graylog` for centralized logs
* `Redis` for distributed cache
* `Spring Security` for authentication

Maybe: 
* `MongoDB` as PoC for denormalized data 
* `WebSockets over STOMP` as PoC for bidirectional high-performance communication
* `JSON Web Tokens` stateless security sessions

**Testing**

At the moment there are no tests. They will also be added. All tests will be written using Spock and Groovy.

3 types of tests:

* Unit
* Integration
* End-to-end tests

**P.S. Yes, I write tests alongside the functionality. So think of the current state of this demo project as an unmerged branch of some new functionality that I'm giving you access to for an early preview (MVP).** 

When I add the tests, I'll remove this section and add a git tag with the version ;). 

**Security and authentication**

For the same reason as for testing, I haven't added security and authentication yet.