/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kaanunsal.ilservice.exception;

/**
 *
 * @author kaan.unsal
 */
public class IlAlreadyExistsException extends RuntimeException{
    
     public  IlAlreadyExistsException (String message)
    {
        super(message);
    }
    
}
