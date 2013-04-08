<?php echo '<?xml version="1.0" encoding="utf-8"?>' ?>
<?php echo "\n"?>
<?php echo '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">' ?>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>S'more</title>
	<meta http-equiv="Content-Type"
		content="text/html; charset=utf-8" />
	<cross-domain-policy>
  		<site-control permitted-cross-domain-policies="all"/>
		<allow-http-request-headers-from domain="*" headers="*"/>
		<allow-access-from domain="*" />
	</cross-domain-policy>
	<link href="./web.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<?php

$PORT = 20222;
$HOST = "pea.cs.colostate.edu";

$socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("ERROR: Could not create socket\n");

$connect = socket_connect($socket, $HOST, $PORT) or die("ERROR: Could not connect to host\n");

$message = socket_read($socket, 10000, PHP_NORMAL_READ) or die("ERROR: Failed to read from socket\n");

echo $message;

?>
</body>
</html>
