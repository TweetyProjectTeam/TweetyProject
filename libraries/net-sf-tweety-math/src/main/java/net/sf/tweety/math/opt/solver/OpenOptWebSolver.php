<?
        $apikey = "some API key";

        $param_apikey = !empty($_POST['apikey']) ? $_POST['apikey'] : false;
        $param_script = !empty($_POST['script']) ? $_POST['script'] : false;

        if($param_apikey != $apikey){
        	echo "wrong apikey";
        }else{   
            $tmpfile = tempnam("/tmp", "oo");
            $filehandle = fopen($tmpfile, "w");
            fwrite($filehandle, $param_script);
            fclose($filehandle);    
            echo system("python " . $tmpfile);
            unlink($tmpfile);
        }
?>