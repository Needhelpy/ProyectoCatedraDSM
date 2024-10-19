<?php

class Usuarios extends Validator
{
    //Declaracion de variables
    private $id_usuario = null;
    private $nombre_usuario = null;
    private $apellido_usuario = null;
    private $email_usuario = null;
    private $clave = null;
    private $id_estado_usuario = null;

    /*
    *   Métodos para validar y asignar valores de los atributos.
    */

    public function setId($value)
    {
        if ($this->validateNaturalNumber($value)) {
            $this->id_usuario = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getId()
    {
        return $this->id_usuario;
    }

    public function setNombre($value)
    {
        if ($this->validateAlphabetic($value, 1, 50)) {
            $this->nombre_usuario = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getNombre()
    {
        return $this->nombre_usuario;
    }

    public function setApellido($value)
    {
        if ($this->validateAlphabetic($value, 1, 50)) {
            $this->apellido_usuario = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getApellido()
    {
        return $this->apellido_usuario;
    }

    public function setEmail($value)
    {
        if ($this->validateEmail($value)) {
            $this->email_usuario = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getEmail()
    {
        return $this->email_usuario;
    }

    public function setClave($value)
    {
        if ($this->validatePassword($value)) {
            $this->clave = password_hash($value, PASSWORD_DEFAULT);
            return true;
        } else {
            return false;
        }
    }

    public function getClave()
    {
        return $this->clave;
    }

    public function setIdEstado($value)
    {
        if ($this->validateNaturalNumber($value)) {
            $this->id_estado_usuario = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getIdEstado()
    {
        return $this->id_estado_usuario;
    }

    //Operaciones de gestion de usuarios
    // Verificar si el email existe
    public function checkEmail()
    {
        $sql = 'SELECT id_usuario, clave FROM usuarios WHERE email_usuario = ?';
        $params = array($this->email_usuario);
        $data = Database::getRow($sql, $params);
        
        // Si el correo es encontrado, se guarda el id del usuario en el objeto actual
        if ($data) {
            $this->id_usuario = $data['id_usuario'];
            return true;
        } else {
            return false;
        }
    }

    // Comprobar si la contraseña es correcta
    public function checkPassword($password_input)
    {
        // Asegurarse de que el email ya ha sido verificado y el id_usuario está disponible
        if ($this->id_usuario) {
            $sql = 'SELECT clave FROM usuarios WHERE id_usuario = ?';
            $params = array($this->id_usuario); // Se usa el id del usuario
            $data = Database::getRow($sql, $params);

            // Si se encuentra la contraseña en la base de datos, se verifica
            if ($data) {
                if (password_verify($password_input, $data['clave'])) {
                    return true; // Contraseña válida
                } else {
                    return false; // Contraseña incorrecta
                }
            } else {
                return false; // No se encontró la clave
            }
        } else {
            return false; // El id_usuario no está configurado
        }
    }

    public function createRow()
    {
        $sql = 'INSERT INTO usuarios(nombre_usuario, apellido_usuario, 	email_usuario, clave)
                VALUES(?, ?, ?, ?)';
        $params = array($this->nombre_usuario, $this->apellido_usuario, $this->email_usuario, $this->clave);
        return Database::executeRow($sql, $params);
    }
}


?>