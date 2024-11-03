
## Procedimentos para rodar a aplicação


A aplicação como pré-requisito necessita do docker instalado.

Para iniciar a mesma, com o shell (Linux) ou o cmd (Windows) aberto, digite o comando:

Linux:

    ./run

Ou Windows:

    .\run.bat


Ao executar esse script, o mesmo fará:


* Compilar o código;
* Executar os testes automatizados;
* Criar o jar;
* Gerar a imagem docker;
* Executar a aplicação via docker e suas dependências;

Para interromper a execução, tecle *CTRL* + *C*

Em seguida execute:

Linux:

    ./stop

Windows:

    .\stop.bat

Após a inicialização da aplicação, pode-se acessá-la pelos endpoints ou via Swagger:

**http://localhost:8080/swagger-ui.html**


## Utilizando a aplicação

Existem 5 endpoints:

### Abertura de Pauta

É por meio dela que são criadas as pautas em que ocorrerão os votos. A mesma tem um intervalo de tempo para voto, o mesmo por padrão é de 60 segundos (1 minuto)

Para criar a pauta, é utilizado o endpoit **/agenda** usando o método POST com o payload como o exemplo a seguir:


    {
        "id": "001",
        "topic": "Tópico da pauta",
        "description": "Descrição da pauta",
        "begin": "2024-11-03T09:57:25.515Z",
        "durationInSeconds": 60
    }

Exemplo usando o curl:

    curl -X 'POST' \
    'http://localhost:8080/agenda' \
    -H 'accept: */*' \
    -H 'Content-Type: application/json' \
    -d '{
        "id": "001",
        "topic": "Tópico da pauta",
        "description": "Descrição da pauta",
        "begin": "2024-11-03T09:57:25.515Z",
        "durationInSeconds": 60
    }'


### Consulta da pauta/resultado dos votos

Retorna os dados da pauta em conjunto com o resultado dos votos.

Para consultar, existem 2 endpoints, o primeiro usa o *id* da pauta como chave de consulta, o segundo usa o *id*, o *tópico* e a *descrição* para fazer a busca.


**/agenda/id/{id}** - Método GET, sendo *{id}* o código da pauta.

Exemplo de retorno:

    [
        {
            "begin": "2024-11-01T17:51:04.606",
            "durationInSeconds": 43200,
            "id": "001",
            "topic": "string",
            "description": "string",
            "end": "2024-11-02T05:51:04.606",
            "summary": {
                "Sim": 1
            }
        }
    ]

Exemplo de chamada com o curl:

    curl -X 'GET' \
    'http://localhost:8080/agenda/id/001' \
    -H 'accept: */*'


**/agenda** - Método GET, aceita os seguintes filtros via query param:

* id - Código da pauta;
* topic - Tópico da pauta;
* description - Descrição da pauta;

O payload de retorno é o mesmo.

Exemplo de chamada com o curl:

    curl -X 'GET' \
    'http://localhost:8080/agenda?topic=Tópico&description=Descricao&id=001' \
    -H 'accept: */*'

Os filtros são opcionais, caso não haja nenhum filtro, esse endpoint retornará todas as pautas

### Voto

**/vote** - Método POST

Cadastra o voto na pauta, o voto precisa ser válido:

* id é o CPF;
* o voto deve ser enviado no período em que a pauta está aberta;
* O mesmo CPF só pode votar 1x na pauta;
* O voto só pode ser *Sim* ou *Não*;

Exemplo de payload:

    {
        "id": "07068093868",
        "agendaId": "001",
        "vote": "Sim"
    }

Sendo agendaId como o id da pauta;

Exemplo com curl:

    curl -X 'POST' \
        'http://localhost:8080/vote' \
        -H 'accept: */*' \
        -H 'Content-Type: application/json' \
        -d '{
            "id": "07068093868",
            "agendaId": "001",
            "vote": "Sim"
        }'


### Checagem de CPF

Verifica se o CPF é válido, retornando: **ABLE_TO_VOTE** ou **UNABLE_TO_VOTE**

**/cpf/{cpf}** - Método GET, sendo*{cpf}* o número do cpf há ser avaliado.

Exemplo com curl:

    curl -X 'GET' \
    'http://localhost:8080/cpf/07068093868' \
    -H 'accept: */*'


## Arquitetura

Utilizado Spring Boot, Docker, WebFlux, MongoDB e Kafka.

Para persistência dos dados foi escolhido o MongoDB, executando 3 instâncias do mesmo, sendo o nó primário para escrita e os secundários para leitura. Dessa maneira a aplicação pode escalar para milhares de transações por segundo, em conformidade com a *Tarefa bônus 3*.

O Kafka como serviço de *event streaming*, todas as requisições para persistência passam por um tópico dele, e o respectivo *Consumer* se encarrega de validar e persistir os dados no banco de dados, ganhando suporte a *back pressure*, *exactly-once*, balanceamento no processamento, cumprindo a *Tarefa bônus 3*.

Os endpoints são reativos, utilizando o WebFlux, a interface com o Mongo também é reativa, assim evitando problemas de performance.

## Pontos importantes

* As classes DTO e persistência usam *bean validation*, evitando sujeira nos dados e facilitando as validações;
* No endpoint POST de pautas **/agenda** é usado a anotação *@Valid* realizando a validação do payload de request, evitando já na chamada o processamento desnecessário;
* No endpoint POST de votos **/agenda** não foi usado o *@Valid* pois era necessário realizar a validação de CPF via chamada externa (simulada pelo **/cpf/{cpf}**), foi realizada a chamada do bean validation de forma programática;
* Os endpoints podem lançar as devidas exceções, conforme as validações e regras de negócio;
* O payload de resposta do endpoint **/cpf**, poderia retornar apenas 1 (ABLE_TO_VOTE) e 0 (UNABLE_TO_VOTE), economizando tráfego de rede e dimunuindo os custos com o provedor de nuvem


Quanto ao versionamento, em relação aos endpoints, só seria necessário se houvesse uma quebra de contrato (modificação substancial nos payload). Nesse caso poderia ser colocado a versão no endpoint: **/agenda/v2/**, mantendo o endpoint original como estava.

