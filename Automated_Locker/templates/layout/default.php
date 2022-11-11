<!DOCTYPE html>
<html>
<head>
    <?= $this->Html->charset() ?>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
        <?= $this->fetch('title') ?>
    </title>

    <!-- <link href="https://fonts.googleapis.com/css?family=Raleway:400,700" rel="stylesheet"> -->
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <?= $this->Html->css(['normalize.min', 'milligram.min', 'cake','menu']) ?>
    
    <?=$this->Html->meta('logo_icon.ico','/img/logo_icon.ico',['type' => 'icon']);?>
    <?= $this->fetch('meta') ?>
    <?= $this->fetch('css') ?>
    <?= $this->fetch('script') ?>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm">
<?php
    if($this->Identity->get("Role_ID") == 1 || $this->Identity->get("Role_ID") == 3) {
    echo "<a class='navbar-brand ml-1' href=".$this->Url->build('/Dashboard/owner').">";
    }
    if($this->Identity->get("Role_ID") == 2) {
        echo "<a class='navbar-brand ml-1' href=".$this->Url->build('/Dashboard/worker').">";
        }
?>
<img src='<?=$this->Url->Image("logo.png")?>' class="p-0 m-0" style="width:80px;height:75px;"></a>
<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo03" aria-controls="navbarTogglerDemo03" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse ml-4 mt-3" id="navbarTogglerDemo03">
    <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
    <?php 
    if($this->Identity->get("Role_ID") == 1) // role as owner (1) (default role)
    { 
    echo '<li class="nav-item ml-1 mr-3 mb-0 active">
            <a class="nav-link" href='.$this->Url->build("/Dashboard/owner").'>Owner Dashboard <span class="sr-only">(current)</span></a>
        </li>';
    }else if($this->Identity->get("Role_ID") == 2 ) // Role as worker (2)
    {
    echo '<li class="nav-item ml-1 mr-3 mb-0 active">
            <a class="nav-link" href='.$this->Url->build("/Dashboard/worker").'>Worker Dashboard <span class="sr-only">(current)</span></a>
        </li>';
    }
    if($this->Identity->get("Role_ID") == 3) // Role admin (3)
    {
        echo '<li class="nav-item ml-1 mr-3 mb-0 active">
        <a class="nav-link" href='.$this->Url->build("/Dashboard/owner").'>Owner Dashboard <span class="sr-only">(current)</span></a>
        </li>
        <li class="nav-item ml-1 mr-3 mb-0">
        <a class="nav-link" href='.$this->Url->build("/Dashboard/worker").'>Worker Dashboard <span class="sr-only">(current)</span></a>
        </li>
         <li class="nav-item">
            <a class="nav-link" href='.$this->Url->build("/Company/view").'>Companies</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href='.$this->Url->build("/Users/view").'>User Profiles</a>
        </li>
        </ul>';
    }else if($this->Identity->isLoggedIn()){
        echo ' <li class="nav-item">
        <a class="nav-link" href='.$this->Url->build("/Users/view").'>My Profile</a>
        </li>
        </ul>';
    }else{
        echo ' <li class="nav-item">
        <a class="nav-link"></a>
        </li>
        </ul>';
    }
    ?>
    <ul class="navbar-nav ml-auto">
    <?php 
        if (!$this->Identity->isLoggedIn()) {
            echo
            '<li class="nav-item mr-3">
            <a class="nav-link" href="'.$this->Url->build("/Users/register").'">Register</a>
            </li>
            <li class="nav-item mr-5">
            <a class="nav-link" href="'.$this->Url->build("/Users/login").'">Login</a>
            </li>';   
        }else{
            echo
            '<li class="nav-item mr-5">
            <a class="nav-link text-danger"><b>Hi '. $this->Identity->get("First_Name") . ' ' . $this->Identity->get('Last_Name') .'</b></a>
            </li>
            <li class="nav-item mr-5">
            <a class="nav-link" href="'.$this->Url->build("/Users/logout").'">Logout</a>
            </li>';   
        }
    ?>
    </ul>
  </div>
</nav>
<main class="main">
    <?= $this->Flash->render() ?>
    <?= $this->fetch('content') ?>
</main>
<footer id="footer" class="text-center bg-light mt-5 p-4 bg-dark text-light">
    <span>&copy; Automated Locker System (ALS). All Rights Reserved 2020 - <?= date("Y"); ?></span>
</footer>
<script src="https://cdnjs.cloudflare.com/ajax/libs/web3/1.3.5/web3.min.js"></script>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script src="https://kit.fontawesome.com/3d9c1bf220.js" crossorigin="anonymous"></script>
<script src="../js/employee.js"></script>
<script src="../js/blockchain.js"></script>

</body>
</html>