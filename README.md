# TDF2
Text Data File (TDF) Version 2 for Minecraft server's plugin development.
<br />
Structure similar to default yaml format used by most Minecraft plugins, but directed to more purposes and faster.<br/>
key: pair, for instance, name: UNFISH<br/>
groups, for example:<br/>
[minecraft]<br/>
 [players]#the sub-group or child group of a group is recognized by more space on the left<br/>
 # this is a full-line comment<br/>
 # this is an inline comment#  UNFISH<br/>
   muxua<br/>
# the presentation<br/>
[group1]<br/>
 [group2]<br/>
  data1 (not key-pair type, i.e no colons)<br/>
  data2<br/>
  ...<br/>
# is a list.<br/>
# a list cannot contain key-pair type, when using set(path, value) to add a new group or subgroup but there existed a group with same path<br/>
# a new group which share the same path will be created but serves the other function which a group can have compared to the previous group<br/>
<br/>
# Usage<br/>
# either setting the jar file as a library or create a new class in your project and copy the code in and do some minor changes to have TDF2 on your environment.<br/>
# in your desired class<br/>
# import io.unfish.TDF2<br/>
# in a function body, create a TDF2 object: TDF2 <name, e.g tdf> = new TDF2();<br/>
# and feed it with a file content as a string or the file<br/>
# tdf.read(str) or tdf.readFromFile(java.io.File)<br/>
# you can get the data via tdf.get(path) or tdf.data.get(path)<br/>
# and you can set the data or add new data or overwrite a whole group by<br/>
# tdf.set(path, value)<br/>
# if 'value' contains '\n', then a new list is created or overwrite the existed list/ group<br/>
# tdf.update() is used internally, but you may need it when using tdf.delete(path) as it does not save changes<br/>
# into file's content buffer like set() does<br/>
# tdf.saveToFile() must be invoked if you wish to save changes by set(), delete() into the real file on your computer.<br/>
