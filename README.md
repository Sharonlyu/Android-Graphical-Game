# Android-Graphical-Game

An Android graphical adventure game which allows players to design, play, save progress and reload
A welcome homepage at the start of this mobile app shows two buttons of game playing and game editing to guide users into two separate modes.
Game editing:

Major tasks:
1) A page class with associated functions (including add, switch, rename and delete)
2) A shape class with associated functions (include add, change property, copy, paste, delete,
cut and set scripts)
3) A database to save game which is to be used in player mode
4) A custom view for displaying how the user interacts with features above responsively.

Features supported:
1) Game drop down menu:
● Clear Database: Once the menu item is clicked, all the records in the database are
cleared. Note that if there’s no record in the database, it will not clear the
database. Instead, it will pop a toast saying that “The database is empty”.
● Import Game: The editor can type in the gameInfo string in the EditText area.
Once it’s confirmed, this gameinfo will be saved in the database, with the name of
importedGame.
● Export Game: When the menu item is clicked, a “sending email” pop window will
appear. The saved json string of the game is shown on the email body, which can
be changed by the user. The user can choose to enter the to/cc email address.
Once the “send” button is clicked, the user will be redirected to Gmail to send the
email. However, this function on the emulator might be delayed.

2) Page drop down menu:
● Create a new page: through a popup window, the editor will first see a editorText
with a default generated name such as “Page2”. The editor can type in the new
page name as they desire. Then, he/she can hit the confirm button to create, or the
cancel button to cancel. Note that the name must be one word with no space, and
must not be the same as the name of other pages.
● Delete current page: The editor can hit the confirm button to delete, or the cancel
button to cancel in the popup window. Note that if current page is page1, the
confirmation will not delete the page. Instead, it will pop a toast saying that “Can't
delete the first page”.
● Rename page: The editor can type in the page name they want for the current
page in a popup window. Similarly, the name must be one word with no space,
and must not be the same as the name of other pages.

● Switch page: A popup window will be shown. All pages will be listed and labeled
with a number greater than 1. The editor can simply type in the number of the
page they want to go to and click confirm. If the input number is not the label of
any page. A toast "Page doesn't exist!" will be shown to the user:

3) Shape drop down menu:
● Add shape: through a dialog window with all shape options listed. When selecting
the shape, it can be moved and resized with blue outline highlighted.
● Change property: through a pop window to set geometry, name, movability,
visibility and display scripts.
● Change text: set text and font size only for text (not image)
● Delete/copy/paste/cut shapes

4) Script drop down:
● Show script: A popup window will present the current script of the selected shape.
The editor needs to click the ok button to close the popup window. Note that if no
shape is selected, the toast “No shape selected!” will be shown and the window
will not be popped.
● On click: On click will have a submenu with “goto”, “play”, “hide” and “show”
features. If goto is selected, a popup window will be shown with all pages listed.
The editor needs to type in the page label number that they want to go to. If play
is selected, a popup window will be shown with all sound names listed as radio
buttons. If hide/show is selected, all shapes in all pages will be listed and labeled
with numbers. Type in the number of the shape you want to hide/show. 
● On enter: On enter will have a submenu with “play”, “hide” and “show” features.
The editor needs to type in the page label number that they want to go to. If play
is selected, a popup window will be shown with all sound names listed as radio
buttons. If hide/show is selected, all shapes in all pages will be listed and labeled
with numbers. Type in the number of the shape you want to hide/show. 
● On drop: On drop will have a submenu with “goto”, “play”, “hide” and “show”
features. After either one of these four actions is clicked, a popup window will be
shown with all the shapes in all pages that will be chosen the onDrop shape. Then,
if goto is selected, a popup window will be shown with all pages listed. The editor
needs to type in the page label number that they want to go to. If play is selected,
a popup window will be shown with all sound names listed as radio buttons. If
hide/show is selected, all shapes in all pages will be listed and labeled with
numbers. Type in the number of the shape you want to hide/show.
5) Save button working with database:
Each game is saved as a json string in the database. When the save button is clicked, the
user will be prompted to name the game through a pop up window. The game is saved in
the database by clicking the “confirm” button. The game will also be saved as a json file
in the local directory, the information of which is shown in a Toast. The game name is
displayed as a TextView on the left lower corner of the screen. All the changes will be
updated in the database.

Extension of the game editor:
1) Error checking:
● Make sure the user selects the legal shape and page name and cannot select a
name which does not exist.
● Only when a shape is selected can the pop-up window of change property
appears.
● In the pop-up window where to choose page name or shape name, user can only
enter a number
● Ensure unique name for all pages and unique name for all shapes in all pages.
2) Resize image when dragging the lower left corner of the image
3) Copy/paste/cut for shapes with all assigned properties (except for location copied)
4) Users can export the game as a Json string and send via email; a json file of the current
game is also saved in the local directory, which can be accessed using a file explorer.
5) Users can also import the game Json string, which will be saved in the database and
parsed in the player side
6) Toast messages for all success but not visible result and failure operation.


Game playing:
Major tasks:
1) A welcome page and a main menu popup window to choose whether the user wants to
create a new game or play a game
2) Ability to read the database created from the Game Editor and create a playable game
from the database
3) A custom view to display the page and shape classes, identify the starting page, create an
independent inventory area to put draggable shapes, and read and execute the scripts of
all shapes in the game
4) An inventory class to create the inventory area as well as adding, removing, resizing, and
positioning shapes (see Extensions)
Features supported:
1) Main game features:
● Read the status of all pages and all shapes in a game and display the visible
shapes on the current page
● Construct a library for both images and sounds, and link the shapes to the library
and “play” scripts to the sounds
● A separate inventory area allowing movable shapes to be stored in, and always
display shapes in inventory regardless of the current page
● Display an example game stored in the custom view for the user to play even
though no game is imported. The example game is the same as that shown in the
assignment handout
2) Database drop down menu:
● Load Other Games From Database: A database that loads game pages, shapes and
scripts saved by editors. Designed a popup window to choose the game that users
want to select and play. Although there are a maximum of four choices shown on
the menu, if there are more games to be loaded from the database, the newer ones
will overwrite the older ones.
Extension of the game player:
1) Background images for main menu, pages, and the possessions area
2) Support for rich text. The game supports texts with different text sizes created in the
game editor
3) Adaptive possessions area. The inventory reads the dimensions of the device, and fills the
bottom of the device with a predetermined “inventory height”
4) Shapes could automatically shrink and position themselves when dragged to the
possessions area:
● Resize the shapes if their height is larger than the inventory height
● Reposition the shapes to the left edge. If there are multiple shapes in the
inventory, they will have a predetermined gap between them
5) Fancy shape drawings without images. When a shape is selected, blue outlines will be
shown around the selected shape
6) “Transition” effects when switching between pages. When switching pages, the previous
page will slide down to show another page
7) “Ambient” sounds playing in the background, and an image wandering around the world
on its own
8) Error checking in player custom view:
● Checks for duplicate page and shape names when a new game is loaded. The
game will not start and show a toast describing the error as well as “Invalid
Game” drawn in the custom view
● Checks if scripts have shape, page and sound names are correctly linked to the
objects in the game and library. Shows a toast of the error when a name does not
have corresponding shape/page/sound
