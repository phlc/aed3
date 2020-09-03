/*
Ciencia da Computacao - Pucminas
AED3 - manha
Pedro Henrique Lima Carvalho
651230
*/

/*
Interface Registro
*/

import java.io.IOException;

interface Registro{
   
   /*
   getID - retorna o ID de um objeto
   @return int id
   */
   public int getID();
   
   /*
   setID - atribui um ID para um objeto
   @param int n 
   */
   public void setID(int n);

   /*
   toByteArray - retorna o conteudo do objeto com byte[]
   @return byte[] ba
   */
   public byte[] toByteArray() throws IOException;

   /*
   fromByteArray - preenche o objeto a partir de um byte[]
   @param byte[] ba
   */
   public void fromByteArray(byte[] ba) throws IOException;

   /*
   chaveSecundaria - retorna a chave secundaria;
   @return String da chave secundaria
   */
   public String chaveSecundaria();
   
}
