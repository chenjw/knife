Home                {#mainpage}
========

# Summary

Knife is a command-line java runtime diagnostic tool like [Btrace](http://kenai.com/projects/btrace/). And provides many features btrace not support.

# Features

Similarities with btrace:

1. Support runtime attach to the jvm.
2. Can trace a method invocation. And print out the arguments and result.
3. Can record the time-consuming of any invocation. And print it out.

Features btrace not support:

1. A command line interactive form. Without having to write the scripts.
2. Can find any object in the heap. 
3. Can view any fields in an object. And change the field values.
4. Can invoke and trace any method of an object.
5. Can find any reference to an object.

# Download

https://github.com/chenjw/knife/downloads

# How to start?
1. download knife-XXX.tar.gz
2. tar -zxvf knife-XXX.tar.gz
3. sh knife.sh

## Attach to local jvm

![attach](http://g.p309.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a44121282b615.jpg)

Choose the jvm and attach.

![attach](http://m.p310.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_5012aff83dee0797.jpg)

# Commands

## find

This command is used to find loaded classes in jvm.

And can also find instances of a class in the heap by the class name.

~~~~~~~~~~
find <find-expretion>
~~~~~~~~~~

 <h3>find-expretion</h3> 

className or class index.

> find classes whose name like '%Service%'

![attach](http://m.p310.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_5012affac0374289.jpg)

> find instances of class 'com.chenjw.knife.server.test.impl.ApplyServiceImpl' in the heap.
> can also short for 'find 15' , '15' means the index of the last find result.

![attach](http://m.p310.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_5012aff916943541.jpg)

## cd

Mark an object as the target object. Some commands like 'invoke' and 'ls' will apply to the target object.

Anywhere an object appears with a mark '@num' (maybe the result of 'find', or the param of 'invoke'), it can be mark as the target object using the command 'cd'.

~~~~~~~~~~
cd <object-id>
~~~~~~~~~~

> Into the instance of class 'com.chenjw.knife.server.test.impl.ApplyServiceImpl'

![attach](http://c.p312.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_5012b6854c961217.jpg)


## ls

This command is used to list the fields of the target object, or to list the methods of the target object.

~~~~~~~~~~
ls [-f] [-m] [<classname>]
~~~~~~~~~~

 <h3>classname</h3>    
                 
Set <classname> to find static fields or methods , if <classname> not set , will apply to target object.

 <h3>-f</h3>       
                     
List fields of target object/class, including static , instance fields, and the fields defined in superclass.

 <h3>-m</h3>     
                    
List methods of target object/class, including static , instance methods, and the methods defined in superclass.

> list fields of target object. Include both static and instance fields

![attach](http://i.p311.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_5012b89e5079d859.jpg)

> list methods of target object. Include both static and instance methods.

![attach](http://i.p311.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_5012b89d1afdd662.jpg)

## set

Change field value.

~~~~~~~~~~
set [-s] <fieldname> <new-value>
~~~~~~~~~~

 <h3>fieldname</h3>

Input 'package.TestClass.field1' means static field or 'field1' means both static and no-static field of target object.

 <h3>new-value</h3>                     

An expretion which will transfer to object by json tool, or '@1' means direct the object by id.

 <h3>-s</h3>

Force set to static field, to avoid misunderstanding


> Change the field value by json expretion.

![attach](http://f.p309.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a1d4c87e96510.jpg)

> Change the field value to an exist object.

![attach](http://f.p309.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a1d4d5a84e282.jpg)


## invoke

This command is used to invoke a method. And trace the invocation info.

~~~~~~~~~~
invoke [-f <filter-expretion>] [-t] <invoke-expretion>
~~~~~~~~~~

 <h3>invoke-expretion</h3>

Input 'package.ClassName.method(param1,param2)' to invoke static method, or 'method(param1,param2)' to invoke the method of target object. The params support json format.

 <h3>-f <filter-expretion></h3>         

Set <filter-expretion> to filter the invocation output you dont care.

 <h3>-t</h3>                            

Is need output the method trace of the invocation.

> just invoke, not trace

![attach](http://m.p312.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a28382528f769.jpg)

> invoke and trace

![attach](http://b.p309.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a308d7fdb5885.jpg)

> invoke and trace and filter the invocation name.

![attach](http://d.p311.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a31b9950c5211.jpg)

## trace

This command is used to wait and trace an invocation in the jvm.

~~~~~~~~~~
trace [-f <filter-expretion>] [-n <trace-num>] [-t] <trace-expretion>
~~~~~~~~~~

 <h3>trace-expretion</h3>

Input 'package.ClassName.method' to trace static method, or 'method' to trace the method of target object.

 <h3>-f <filter-expretion></h3>         

Set <filter-expretion> to filter the invocation output you dont care.

 <h3>-t</h3>                            

Is need output the method trace of the invocation.

 <h3>-n <trace-num></h3>                            

Trace times.

## ref

Find objects who hold the reference of target object in the heap.

~~~~~~~~~~
ref <object-id>
~~~~~~~~~~

 <h3>object-id</h3>                     

A num as the object id.

> find the references to an object by object id.

![attach](http://e.p310.56.com/photo2video/upImg/d1/43/84/orgin_xl-95903708_501a353a68ccb107.jpg)


