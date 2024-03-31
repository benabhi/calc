# calc

Calculadora simple escrita en el lenguaje de programación `clojure` capaz de
ejercer operaciones aritméticas básicas (suma, resta, división y multiplicación)

## Objetivo

Programa creado con intenciones didacticas para aprender el lenguaje y para
entrega de trabajo en asignatura "Programación Lógica" de la carrera de analís
de sistemas.

## Instalación

Clonamos el repositorio de github, ingresamos en el y compilamos la aplicación.

```shell
git clone https://github.com/benabhi/calc && cd calc
lein uberjar
```

## Usage

Una vez compilado el jar, en la carpeta `./target/uberjar` esta el archivo que
necesitamos ejecutar para inicializar la aplicación.

```shell
java -jar ./target/uberjar/calc-0.1.0-SNAPSHOT-standalone.jar
```
