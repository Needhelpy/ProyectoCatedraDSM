<?php
require_once('../helpers/database.php');
require_once('../helpers/validator.php');
require_once('../models/notas.php');

if (isset($_GET['action'])) {
    session_start();
    $notas = new Notas;
    $result = array('status' => 0, 'message' => '', 'exception' => '', 'dataset' => null);

    switch ($_GET['action']) {
        case 'ping':
            // Este caso simplemente devuelve un mensaje de prueba.
            $result['status'] = 1;
            $result['message'] = 'La API de Notas está funcionando correctamente';
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/notas.php?action=create
        case 'create':
            $_POST = $notas->validateForm($_POST);
            if (!$notas->setTitulo($_POST['titulo'])) {
                $result['exception'] = 'Título incorrecto';
            } elseif (!$notas->setContenido($_POST['contenido'])) {
                $result['exception'] = 'Contenido incorrecto';
            } elseif (!$notas->setIdUsuario($_POST['usuario'])) {
                $result['exception'] = 'Usuario incorrecto';
            } elseif ($notas->createRow()) {
                $result['status'] = 1;
                $result['message'] = 'Nota creada correctamente';
            } else {
                $result['exception'] = Database::getException();
            }
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/notas.php?action=readAll
        case 'readAll':
            if (!$notas->setIdUsuario($_POST['usuario'])) {
                $result['exception'] = 'Identificador incorrecto';
            } elseif ($result['dataset'] = $notas->readAll()) {
                $result['status'] = 1;
            } elseif (Database::getException()) {
                $result['exception'] = Database::getException();
            } else {
                $result['exception'] = 'Identificador inexistente';
            }
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/notas.php?action=readOne
        case 'readOne':
            if (!$notas->setId($_POST['id'])) {
                $result['exception'] = 'Identificador incorrecto';
            } elseif ($result['dataset'] = $notas->readOne()) {
                $result['status'] = 1;
            } elseif (Database::getException()) {
                $result['exception'] = Database::getException();
            } else {
                $result['exception'] = 'Identificador inexistente';
            }
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/notas.php?action=update
        case 'update':
            $_POST = $notas->validateForm($_POST);
            if (!$notas->setId($_POST['id'])) {
                $result['exception'] = 'Identificador incorrecto';
            } elseif (!$notas->setTitulo($_POST['titulo'])) {
                $result['exception'] = 'Título incorrecto';
            } elseif (!$notas->setContenido($_POST['contenido'])) {
                $result['exception'] = 'Contenido incorrecto';
            } elseif ($notas->updateRow()) {
                $result['status'] = 1;
                $result['message'] = 'Nota actualizada correctamente';
            } else {
                $result['exception'] = Database::getException();
            }
            break;

            //api: http://localhost/ProyectoCatedraDSM/api/controller/notas.php?action=delete
        case 'delete':
            if ($notas->setId($_POST['id'])) {
                if ($notas->deleteRow()) {
                    $result['status'] = 1;
                    $result['message'] = 'Nota eliminada correctamente';
                } else {
                    $result['exception'] = Database::getException();
                }
            } else {
                $result['exception'] = 'Identificador incorrecto';
            }
            break;
        default:
            $result['exception'] = 'Acción no disponible';
    }

    header('content-type: application/json; charset=utf-8');
    print(json_encode($result));
} else {
    print(json_encode(array('message' => 'Recurso no disponible')));
}
?>