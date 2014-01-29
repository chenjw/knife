Home (en)                {#mainpage-en}
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

https://sourceforge.net/projects/knife-download/files/

# How to start?
1. download knife-XXX.tar.gz
2. tar -zxvf knife-XXX.tar.gz
3. sh knife.sh

## Attach to local jvm

![Attach](attach.png)

Choose the jvm and attach.

![Connected](connected.jpg)

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

![Find Service](find_service.jpg)

> find instances of class 'com.chenjw.knife.server.test.impl.ApplyServiceImpl' in the heap.
> can also short for 'find 15' , '15' means the index of the last find result.

![Find Target Service](find_target_service.jpg)

## cd

Mark an object as the target object. Some commands like 'invoke' and 'ls' will apply to the target object.

Anywhere an object appears with a mark '@num' (maybe the result of 'find', or the param of 'invoke'), it can be mark as the target object using the command 'cd'.

~~~~~~~~~~
cd <object-id>
~~~~~~~~~~

> Into the instance of class 'com.chenjw.knife.server.test.impl.ApplyServiceImpl'

![cd](cd.jpg)


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

![ls -f](ls_f.jpg)

> list methods of target object. Include both static and instance methods.

![attach](ls_m.jpg)

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

![attach](set.jpg)

> Change the field value to an exist object.

![attach](set_by_object_id.jpg)


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

![attach](invoke.jpg)

> invoke and trace

![attach](invoke_t.jpg)

> invoke and trace and filter the invocation name.

![attach](invoke_f_t.jpg)

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

![attach](ref.jpg)


