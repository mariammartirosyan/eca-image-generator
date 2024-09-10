# ECA Image Generating Microservice

The `ms_art` microservice handles the generation of images, communicating with other services via UDP.

It supports the generation of two types of images, with cell calculations performed in parallel:

1. **Black-and-white geometric structures** on which Elementary Cellular Automaton (ECA) is applied.
2. **Colored ECA-generated images**, where the color of each live cell is determined by the states of its neighboring cells.