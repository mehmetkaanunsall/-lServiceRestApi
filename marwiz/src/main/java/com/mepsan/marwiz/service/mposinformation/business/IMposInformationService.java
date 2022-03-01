/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.mposinformation.business;


/**
 *
 * @author emrullah.yakisan
 */
public interface IMposInformationService {

    public String sendInformationLog(String jsonData,String username, String password, String licenceCode,String wsEndPoint);

  

}
