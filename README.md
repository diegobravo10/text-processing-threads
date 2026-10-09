# Procesamiento secuencial y paralelo de textos con Java Threads

Actividad práctica 01 de Computación Paralela. El programa analiza *Don Quijote de la Mancha*  y obtiene:

1. El número total de palabras.
2. El número de apariciones de una palabra (por defecto `quijote`).
3. El número de palabras que contienen una secuencia (por defecto `ción`).

La búsqueda no distingue mayúsculas de minúsculas. Se compara una implementación **secuencial** con una **paralela** que usa directamente la clase `Thread` (`start()` / `join()`) con 2 y 4 hilos, sobre el documento original y sobre una versión ampliada (el texto repetido 100 veces).

## Estructura

```
├── libro.txt                    Texto de entrada
├── src/
│   ├── Main.java                Carga, verificación, benchmark y tabla de resultados
│   ├── Analizador.java          Análisis de un rango [inicio, fin) del texto
│   ├── ProcesadorSecuencial.java
│   ├── ProcesadorParalelo.java  División en bloques + creación/start/join de hilos
│   ├── HiloAnalizador.java      extends Thread, procesa un bloque
│   └── Resultado.java           Conteos y combinación de resultados parciales
└── informe/informe.html         Informe de resultados
```

## Compilar y ejecutar

Desde la raíz del proyecto:

```bash
javac -encoding UTF-8 -d out src/*.java
java -Xmx2g -cp out Main
```

`-Xmx2g` es necesario porque la versión ampliada (~215 millones de caracteres) se construye en memoria.

### Parámetros opcionales

```
java -Xmx2g -cp out Main [archivo] [palabra] [secuencia] [repeticiones] [factorAmpliado]
```

| Parámetro        | Por defecto | Descripción                                   |
|------------------|-------------|-----------------------------------------------|
| `archivo`        | `libro.txt` | Ruta del archivo de texto                     |
| `palabra`        | `quijote`   | Palabra cuyas apariciones se cuentan          |
| `secuencia`      | `ción`      | Secuencia que deben contener las palabras     |
| `repeticiones`   | `5`         | Ejecuciones medidas por configuración         |
| `factorAmpliado` | `100`       | Veces que se repite el texto en el ampliado   |

Ejemplo: `java -Xmx2g -cp out Main libro.txt sancho mente 10 100`

> Si en la consola de Windows las tildes salen mal, ejecuta antes `chcp 65001`.

## Qué hace el programa

1. Lee `libro.txt` en memoria, quita la cabecera y la licencia de Gutenberg y lo pasa a minúsculas.
2. Construye en memoria la versión ampliada.
3. Verifica que los resultados con 2 y 4 hilos coincidan con los del secuencial.
4. Para 1 (secuencial), 2 y 4 hilos: 3 ejecuciones de calentamiento y 5 medidas con `System.nanoTime()`.
   Solo se mide el procesamiento.
5. Imprime en consola la tabla con el tiempo promedio y la aceleración (`S = Ts / Tp`).

## Criterios

- **Palabra**: secuencia continua de letras (`Character.isLetter`), incluidas tildes, ñ y ü.
- **División en bloques**: cada frontera `n·k/N` se adelanta hasta un separador, así ninguna palabra queda partida entre dos hilos.
- Cada hilo guarda su propio resultado parcial (sin estado compartido); al final se suman.

## Resultados obtenidos

Intel Core Ultra 9 185H (16 núcleos / 22 hilos), 32 GB RAM:

| Conjunto | Hilos          | Tiempo promedio (ms) | Aceleración |
|----------|----------------|---------------------:|------------:|
| Original | 1 (secuencial) |                9.067 |        1.00 |
| Original | 2              |                5.034 |        1.80 |
| Original | 4              |                3.769 |        2.41 |
| Ampliado | 1 (secuencial) |              963.830 |        1.00 |
| Ampliado | 2              |              504.253 |        1.91 |
| Ampliado | 4              |              271.058 |        3.56 |

El análisis completo está en [informe/informe.html](informe/informe.html).
