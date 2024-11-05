## Install metrics-server for HPA (Horizontal Pod Autoscaler)

```shell
$ kubectl apply -f metrics-server.yaml && kubectl patch deployment metrics-server -n kube-system --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value":"--kubelet-insecure-tls"}]'
```

Originally from: https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.7.2/components.yaml

## Install Ingress Controller

```shell
$ kubectl apply -f ingress-nginx.yaml
```

Originally from: https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml