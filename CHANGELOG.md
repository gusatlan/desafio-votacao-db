### 2024-11-03

* Documentação em MANUAL.md;
* Criação do docker-compose.yml;
* Ajustes no application.yml;
* Criação do endpoint GET /agenda?;
* Conversão no SummaryAgendaDTO de "YES"/"NO" para "Sim" e "Não";

### 2024-11-01

* Criado CpfClient;
* Criado testes para CpfController e CpfClient;
* Criado teste unitário para VoteType;
* Criado VoteController, VoteService e VoteServiceTest;
* Implementado persistência com Kafka no VoteService;

### 2024-10-31

* Criação das classes DTO: AgendaDTO, VoteDTO, SummaryAgendaDTO;
* Correção das classes AgendaPersist e VotePersist, utilizando LocalDateTime;
* Ajustes e criação de classes de testes unitários;
* Adicionado script compile, compile.bat, make, make.bat, run e run.bat
* Testes unitários/integração para AgendaService;
* Producer e Consumer para persistir AgendaDTO;
* Adicionado validação @CPF para o id das classes VotePersist e VoteDTO;
* Criado CpfController;

### 2024-10-30

* Criação do projeto;
* Configuração no application.yml para Kafka, MongoDB;
* Criado classes de configuração para Kafka, Jackson e Swagger;
* Criado arquivos utilitários para conversões, datas, json e logger;
* Criada classe BuilderTest para instanciação de objetos a serem testados;
* Criado testes unitários para AgendaPersist e VotePersist;
* Criado classes AgendaPersist e VotePersist;
* Criação da interface de repositório para AgendaPersist;
