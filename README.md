# Inventory

Projeto de cadastro de recursos

## Sobre

Sistema criado com SpringBoot e banco H2.

### Configurações

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

### Instalando

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo
