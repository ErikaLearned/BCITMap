# BCITMap

### About
A British Columbia Institute of Technology project by students in the Computer Systems Technology program for the course COMP3737. This project sets out to create a BCIT Burnaby map Android application that with the features:

1. Navigable floorplan for each building 

2. Display a path between a start point and end point (rooms, parking lots, bus stops)
  * Option for speed
  * Option for rainy day (under cover as much as possible)
  
3. Savable paths
  * Class schedule
  
4. Easy to search/find features
  * Water fountain
  * Washroom
  * Locker (saved)
  * Garbage/compost/recycling

### Naming Guidelines
#### General
- Underscore separates significant phrases or words
- Anything normally capitalized is capitalized
```
SE12_nsCoordinate
BCIT_Map
```
#### Variable
- Combine camel case with underscores
- Start with lowercase
```
roomList
roomList_FilteredTo
SE_nwCoordinate
SE12
```
#### Class
- Start words in class names with capital letters
```
BCIT_Map
Directions_Menu
```
#### Methods
- Camelcase
- Start with lowercase unless conflicting with a general guidelines
```
setStartButton
startMap
getSE12
```
