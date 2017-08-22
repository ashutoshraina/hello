# Hello

Starting MiniKube

```
(minikube delete || true) &>/dev/null && \
minikube start --memory 2458 && \
eval $(minikube docker-env)

```

Building Docker images 

```
 sbt clean docker:publishLocal && docker build -t liberty-app liberty
```

Creating Pods

```
kubectl create -f deploy/resources/lagom && kubectl create -f deploy/resources/nginx && kubectl create -f deploy/resources/liberty
```

Listing Services

```
minikube service list
```

Hit  /lagom

