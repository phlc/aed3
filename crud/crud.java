/*
Classe CRUD - Realiza Operacoes CRUD de Arquivo
Cabecalho
   byte 0-5 tipo do arquivo "CRUD"
   byte 6-9 proximo id (int) inicio em 1
Registros
   lapide - 1 byte
   indicador de tamanho - 1 short
   conteudo = byte[]

*/

import java.lang.reflect.Constructor;
import java.io.RandomAccessFile;

class CRUD <T extends Registro>{
//atributos da classe

//atributos
   private Constructor<T> constructor; 
   private String file;

//construtor
   CRUD(Constructor<T> constructor, String file){
      this.constructor = constructor;
      this.file = file;
      RandomAccessFile arq;      

      //Verificar se arquivo com nome ja existe
      try{
         arq = new RandomAccessFile (file, "r");
      }
      //Se nao exisitr criar
      catch (FileNotFoundException novoArq){
         arq = new RandomAccessFile (file, "rw");
         
         //Escreve CRUD no cabecalho para identificar o tipo do arquivo
         arq.writeUTF("CRUD");
         
         //Escreve o proximo id do arquivo (offset 6)
         arq.writeInt(1);

         //voltar cursor para inicio
         arq.seek(0);
      }
      //testa se arquivo do tipo crud
      try{
         String nome = arq.readUTF();
         if (!nome.equals("CRUD"))
            throws new Exception("Arquivo !CRUD");
      }
      catch (Exception e){
         System.out.println(e);
         arq.close();
         this.file = null;
      }
   }

//metodos
   /*
   create - Cria um registro no arquivo
   @param T objeto
   @return int id
   */
   public int create(T objeto){

   }
   
   /*
   read - Ler um objeto do arquivo
   @param int id
   @return T objeto
   */
   public T read(int id){

   }
   
   /*
   update - atualiza um registro
   @param T objeto
   @return boolean true (sucesso) false (falha)
   */
   public boolean update(T objeto){

   }

   /*
   delete - deleta um registro
   @param int id
   @return boolean true (sucesso) false (falha)
   */


}
