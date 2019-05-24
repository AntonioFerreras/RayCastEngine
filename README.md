# RayCastEngine
A 3D engine using the ray casting technique.

![alt text](https://i.imgur.com/MzsNqAw.png)

This is a 3D engine that utilizes the ray casting technique to render the world view.

## How does it work?
It splits the screen into many vertical strips, each of these strips casts out a ray within the camera's FOV. When a ray hits a wall, the traveled by the ray is recorded. Using this distance the projected height of the wall on-screen is calculated and drawn. This means that the further the ray hit, the smaller the wall slice, and the closer the ray hits a wall, the larger the wall slice will be drawn.

## What other features are there?
- Texture mapped walls
- A skybox
- Variable height walls (has issues atm)
- Customizable FOV and resolution

## Why did I make this?
To learn about 2D graphics in Java using game loops. Also to apply my highschool trigonometry knowledge into a programming project. 
