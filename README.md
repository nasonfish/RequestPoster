RequestPoster
=============

RequestPoster is a new way to send mod request data (From ReportRTS - http://github.com/ProjectInfinity/ReportRTS/ ) to a URL.

RequestPoster simply hooks into ReportRTS' "Request Created" event, and sends data to each url defined in the config file via POST.

You can use this data for whatever purpose suits you, though a downside is that you need to have a web server to use it.

I use this plugin to notify users on IRC when a new mod request is created:

```php
<?php
if($_SERVER['REMOTE_ADDR'] == "xxx.xxx.xxx.xxx" || $_SERVER['REMOTE_ADDR'] == "xxx.xxx.xxx.xxx"){ // Check if it's an IP I want to allow.
    $text = sprintf("(ID #%s) A new MODREQ was submitted by %s: '%s'", $_POST['id'], $_POST['name'], $_POST['message']);
    // nick and user data is thrown away since I use a bouncer.
    exec("echo 'NICK nickname
USER nickname nickname nickname :nickname
PASS user:pass
PRIVMSG #channelname :$text
QUIT :Farewell!' | netcat localhost 8080"); // I unstalled netcat, which is like telnet, but it accepts data from stdin.
} else {
    print "I think you're in the wrong place, sorry.";
}
```
However, there's tons you can do with the data beyond that, so don't restrict yourself.

Installation
------------

Setting up this plugin is pretty easy - You can either run the plugin to generate the config file, or create a file named `config.txt` in `/plugins/RequestPoster/`.

Inside that file, you can enter HTTP addresses, and RequestPoster will POST data there. *Comments must precede the entire line with `#` or they will be interpreted as URLs*.

```
http://nasonfish.com/
http://localhost/modreq.php
http://208.68.37.146/stuff.php
# This is a comment!
http://stuff.nasonfish.com/
```


What data is posted?
------------

We post all of the data we recieve from the event sent by ReportRTS. You can see all of the parameters here: https://github.com/nasonfish/RequestPoster/blob/master/src/com/nasonfish/requestposter/RequestTask.java#L89

```yaml
id: The id of the mod request. Integer
message: The message the user specified, encoded for POSTing - /modreq <message>. Example: Hello+world%21+I%27m+a+fish.
modid, modname, modtimestamp: All 0/null, since the mod request has not been claimed yet.
name: The username of the person who sent the request. Example: nasonfish
pitch: The direction the user is facing, side to side. Integer.
status: I believe it is something about if the request is claimed or on hold or not. It should be 0.
timestamp: When the request was created. Java's `System.currentTimeMillis() / 1000;`. `Long`.
world: World name.
x, y, z: Coordinates. Integers.
yaw: Like pitch, the direction the user is facing, but on the up/down axis.
```
