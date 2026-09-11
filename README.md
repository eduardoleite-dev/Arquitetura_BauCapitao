# Bau Foundation

Biblioteca base para APIs Spring Boot do ecossistema Bau do Capitao.

## Responsabilidades

- Politica de autorizacao por proprietario e role master.
- Geracao e validacao de tokens JWT.
- Criptografia autenticada AES-GCM.

A biblioteca nao conhece entidades de dominio e nao armazena segredos. A aplicacao fornece implementacoes de `FoundationUser` e `FoundationUserLookup`; o ambiente fornece chaves e configuracoes.

## Uso local no monorepo

O modulo e incluido pelo `settings.gradle` raiz:

```gradle
include 'foundation'
```

Uma aplicacao pode depender dele com:

```gradle
implementation project(':foundation')
```

## Evolucao para pacote privado

Quando estabilizado, publique o artefato `com.baudocapitao:foundation` em um repositorio Maven privado, como GitHub Packages ou Azure Artifacts, e consuma por versao.
