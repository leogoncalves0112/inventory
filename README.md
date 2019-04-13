# Inventory

Projeto de cadastro de recursos

## Sobre

Api criada com SpringBoot e banco H2.

### Configurações

A imagem do equipamento está sendo salvo no S3 da Amazon, para configurar os dados do S3 deverá configurar no arquivo application.properties nas seguintes chaves:

```
aws.s3.accessKey=accessKey
aws.s3.secretKey=secretKey
aws.s3.bucketName=bucketName
```

Para configuração do envio do email, deve-se configurar no arquivo application.properties nas seguintes chaves:

```
spring.mail.host=smtp.gmail.com
spring.mail.port=465
spring.mail.username=xxx@gmail.com
spring.mail.password=xxx
spring.mail.properties.mail.smtp.auth: true
spring.mail.properties.mail.smtp.starttls.enable: true
spring.mail.properties.mail.smtp.starttls.required: true
spring.mail.properties.mail.smtp.ssl.enable: true
inventory.mail.to=yyy@gmail.com
```

Para configurar o cálculo de depreciação deve-se informar o valor no arquivo application.properties na seguinte chave:

```
inventory.equipment.depreciation=10
```

Para utilizar o swagger-ui e ver a API só acessar o seguinte endereço:

```
http://localhost:8080/swagger-ui.html
```

### Instalando

Foi criado um docker com o ubuntu + java e a aplicação dentro.

O repositório é o seguinte:

```
https://cloud.docker.com/repository/docker/leogoncalves0112/ubuntu-java-inventory
```

Para realizar o push só rodar:

```
docker push leogoncalves0112/ubuntu-java-inventory:latest
```

Dentro do docker tem a pasta /cin, que dentro contém o jar da aplicação e um script (startup.sh) para levantar o jar com o tomcat embarcado.  

A porta que a aplicação levanta dentro do docker é a 8080, para ser configurado quando rodar o container.
