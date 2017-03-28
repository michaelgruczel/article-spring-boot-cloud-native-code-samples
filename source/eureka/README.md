## Eureka Service on Cloud Foundry

This manual shows how to deploy and use Eureka Service on Cloud Foundry.

### Deploy Eureka Service to CF

    $ mvn clean package
    $ cf push
    $ cf apps
    Getting apps in org maki-org / space development as admin...
    OK

    name            requested state   instances   memory   disk   urls
    eureka-server   started           1/1         256M     1G     eureka-server.192.168.33.10.xip.io

## Expose Eureka Service as a [User-Provided Service](https://docs.cloudfoundry.org/devguide/services/user-provided.html)


    $ cf create-user-provided-service eureka-service -p '{"uri":"http://eureka:changeme@eureka-server.192.168.33.10.xip.io"}'
    $ cf services
    Getting services in org maki-org / space development as admin...
    OK

    name             service         plan   bound apps   last operation
    eureka-service   user-provided

## Bind Eureka Service to an application

    $ cf push foo -p target/foo.jar --no-start
    $ cf bind-service foo eureka-service

Discovery client services (like `foo`) should have the following property in `application.property`

    eureka.client.service-url.defaultZone=${vcap.services.eureka-service.credentials.uri:http://localhost:8761}/eureka/

and in `application-cloud.properties`

    eureka.instance.hostname=${vcap.application.uris[0]}
    eureka.instance.non-secure-port=80
    eureka.instance.metadata-map.instanceId=${vcap.application.instance_id}

A sample application is [here](https://github.com/making/hello-pws/tree/cloud).
