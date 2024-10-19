<?php
require_once('../helpers/database.php');
require_once('../helpers/validator.php');
require_once('../models/usuario.php');

if (isset($_GET['action'])) {
    session_start();
    $usuario = new Usuarios;
    $result = array('status' => 0, 'message' => '', 'exception' => '', 'dataset' => null);
    
    switch ($_GET['action']) {
        case 'ping':
            // Este caso simplemente devuelve un mensaje de prueba.
            $result['status'] = 1;
            $result['message'] = 'La API está funcionando correctamente';
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/usuario.php?action=register
        case 'register':
            $_POST = $usuario->validateForm($_POST);
            if (!$usuario->setNombre($_POST['nombres'])) {
                $result['exception'] = 'Nombres incorrectos';
            } elseif (!$usuario->setApellido($_POST['apellidos'])) {
                $result['exception'] = 'Apellidos incorrectos';
            } elseif (!$usuario->setEmail($_POST['correo'])) {
                $result['exception'] = 'Correo incorrecto';
            } elseif ($_POST['clave'] != $_POST['confirmar']) {
                $result['exception'] = 'Claves diferentes';
            } elseif (!$usuario->setClave($_POST['clave'])) {
                $result['exception'] = 'Clave no válida';
            } elseif ($usuario->createRow()) {
                $result['status'] = 1;
                $result['message'] = 'Usuario registrado correctamente';
            } else {
                $result['exception'] = Database::getException();
            }
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/usuario.php?action=logIn
        case 'logIn':
            $_POST = $usuario->validateForm($_POST);
            if (!$usuario->setEmail($_POST['correo'])) {
                $result['exception'] = 'Correo incorrecto';
            } elseif ($hash = $usuario->checkEmail()) {
                if ($usuario->checkPassword($_POST['clave'])) {
                    $result['status'] = 1;
                    $result['message'] = 'Autenticacion correcta';
                    $_SESSION['id_usuario'] = $usuario->getId();
                    $_SESSION['nombre_usuario'] = $usuario->getNombre();
                    $_SESSION['apellido_usuario'] = $usuario->getApellido();
                    $result['dataset'] = array(
                        'id_usuario' => $usuario->getId()
                    );
                } else {
                    $result['exception'] = 'Clave incorrecta';
                }
            } else {
                $result['exception'] = 'El correo no está registrado';
            }
            break;

        default:
            $result['exception'] = 'Accion no disponible';
    }

    header('content-type: application/json; charset=utf-8');
    print(json_encode($result));
} else {
    print(json_encode(array('message' => 'Recurso no disponible')));
}
?>
