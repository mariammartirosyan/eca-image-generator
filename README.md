# ArtMicroservice

ms_art microservice is responsible for generating images.
The request handling and the calculation of image cells are implemented using the job/worker pattern. 

The microservice supports generating two types of images: 
  1. Black and white geometric structures on which Elementary Cellular Automaton (ECA) is applied.
  2. ECA generated colored images, where the color of every live cell is determined by the states of its neighboring cells.

The architecture design of the microservice allows extensible integration of art generation algorithms 
