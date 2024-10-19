<?php

class Notas extends Validator
{
    //Declaracion de variables
    private $id_nota = null;
    private $titulo = null;
    private $contenido = null;
    private $fecha = null;
    private $id_usuario = null;

    //Metodos para validar y asignar valores de los atributos.
    public function setId($value)
    {
        if ($this->validateNaturalNumber($value)) {
            $this->id_nota = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getId()
    {
        return $this->id_nota;
    }

    public function setTitulo($value)
    {
        if ($this->validateAlphanumeric($value, 1, 50)) {
            $this->titulo = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getTitulo()
    {
        return $this->titulo;
    }

    public function setContenido($value)
    {
        if ($this->validateAlphanumeric($value, 1, 500)) {
            $this->contenido = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getContenido()
    {
        return $this->contenido;
    }

    public function setFecha($value)
    {
        if ($this->validateAlphanumeric($value, 1, 50)) {
            $this->fecha = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getFecha()
    {
        return $this->fecha;
    }

    public function setIdUsuario($value)
    {
        if ($this->validateNaturalNumber($value)) {
            $this->id_usuario = $value;
            return true;
        } else {
            return false;
        }
    }

    public function getIdUsuario()
    {
        return $this->id_usuario;
    }

    //Metodos para el manejo del CRUD

    public function createRow()
    {
        $sql = 'INSERT INTO notas(titulo, contenido, id_usuario) VALUES(?, ?, ?)';
        $params = array($this->titulo, $this->contenido, $this->id_usuario);
        return Database::executeRow($sql, $params);
    }

    public function readAll()
    {
        $sql = 'SELECT id_nota, titulo, contenido FROM notas WHERE id_usuario = ?';
        $params = array($this->id_usuario);
        return Database::getRows($sql, $params);
    }

    public function readOne()
    {
        $sql = 'SELECT id_nota, titulo, contenido FROM notas WHERE id_nota = ?';
        $params = array($this->id_nota);
        return Database::getRow($sql, $params);
    }

    public function updateRow()
    {
        $sql = 'UPDATE notas SET titulo = ?, contenido = ? WHERE id_nota = ?';
        $params = array($this->titulo, $this->contenido, $this->id_nota);
        return Database::executeRow($sql, $params);
    }

    public function deleteRow()
    {
        $sql = 'DELETE FROM notas WHERE id_nota = ?';
        $params = array($this->id_nota);
        return Database::executeRow($sql, $params);
    }

}


?>