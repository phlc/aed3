/*
Classe CRUD - Realiza Operacoes CRUD de Arquivo
Cabecalho
   byte 0-5 tipo do arquivo "CRUD"
   byte 6-9 proximo id (int) inicio em 1
Registros comecao byte 10
   lapide - 1 byte
   indicador de tamanho - 1 short
   conteudo = byte[]

*/
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.lang.reflect.Constructor;
import java.io.RandomAccessFile;

class CRUD <T extends Registro>{
//atributos da classe

//atributos
   private Constructor<T> constructor; 
   private String file;

//construtor
   CRUD(Constructor<T> constructor, String file) throws Exception{
      this.constructor = constructor;
      this.file = file;
      RandomAccessFile arq;      

      //Verificar se arquivo com nome ja existe
      try{
         arq = new RandomAccessFile (this.file, "r");
      }
      //Se nao exisitr criar
      catch (FileNotFoundException novoArq){
         arq = new RandomAccessFile (this.file, "rw");
         
         //Escreve CRUD no cabecalho para identificar o tipo do arquivo
         arq.writeUTF("CRUD");
         
         //Escreve o proximo id do arquivo (offset 6)
         arq.writeInt(1);

         //voltar cursor para inicio
         arq.seek(0);
      }
      //testa se arquivo do tipo crud
      try{
         arq.seek(0);
         String nome = arq.readUTF();
         if (!nome.equals("CRUD"))
            throw new Exception("Arquivo !CRUD");
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
   public int create(T objeto) throws Exception{
      //testar arquivo
      RandomAccessFile arq = new RandomAccessFile(this.file, "r");
      arq.seek(0);
      String nome = arq.readUTF();
      if (!nome.equals("CRUD"))
         throw new Exception("Arquivo !CRUD");
      arq.close();
      
      //abrir para escrita
      arq = new RandomAccessFile(this.file, "rw");
      
      //ler proximo id
      arq.seek(6);
      final int nextId = arq.readInt();
      
      //atualizar id no objeto
      objeto.setID(nextId);

      //Dados para escrita
      byte lapide = 0;
      byte[] ba = objeto.toByteArray();
      short tam = (short)ba.length;
      
      //escrever proximo ID
      arq.seek(6);
      arq.writeInt(nextId + 1);

      //escrever novo registro
      arq.seek(arq.length());
      arq.writeByte(lapide);
      arq.writeShort(tam);
      arq.write(ba);
      arq.close();

      return(nextId);
   }
   
   /*
   read - Ler um objeto do arquivo
   @param int id
   @return T objeto
   */
   public T read(int id) throws Exception{
      //abrir arquivo para leitura
      RandomAccessFile arq = new RandomAccessFile(this.file, "r");

      //ir para inicio dos registros
      arq.seek(10);

      //variaveis
      T objeto = this.constructor.newInstance();
      boolean found = false;

      //ler ate achar ou fim
      try{
         while(!found){
            byte lapide = arq.readByte();
            short tam = arq.readShort();
            byte[] ba = new byte[tam];
            arq.read(ba);
            objeto.fromByteArray(ba);
            if (lapide==0 && id==objeto.getID())
               found = true;
         }
      }
      catch(EOFException end){}
      
      if(!found)
         objeto = null;

      arq.close();
      return(objeto);
   }
   
   /*
   update - atualiza um registro
   @param T objeto
   @return boolean true (sucesso) false (falha)
   */
   public boolean update(T objeto) throws Exception{
      //testar arquivo
      RandomAccessFile arq = new RandomAccessFile(this.file, "r");
      arq.seek(0);
      String nome = arq.readUTF();
      if (!nome.equals("CRUD"))
         throw new Exception("Arquivo !CRUD");
      arq.close();
      
      //abrir para escrita
      arq = new RandomAccessFile(this.file, "rw");
      
      //objeto antigo
      int id = objeto.getID();
      T antigo = read(id);
      byte[] baAntigo = antigo.toByteArray();

      //objeto novo
      byte[] baNovo = objeto.toByteArray();
   
      //novo registro maior que o anterior
      if(baAntigo.length < baNovo.length){
         if(delete(id)){
            arq.seek(arq.length());
            arq.writeByte(0);
            arq.writeShort((short)baNovo.length);
            arq.write(baNovo);
         }
         else
            throw new Exception("Erro ao deletar registro menor");
      }      
      
      //novo registro menor ou igual ao anterior
      else{
         arq.seek(10);
         T buffer = this.constructor.newInstance();
         boolean found = false;

         //ler ate achar ou fim
         try{
            while(!found){
               byte lapide = arq.readByte();
               short tam = arq.readShort();
               byte[] ba = new byte[tam];
               arq.read(ba);
               buffer.fromByteArray(ba);
               if (lapide==0 && id==buffer.getID())
                  found = true;
            }
         }
         catch(EOFException end){}
         
         //verificar se registro foi encontrado
         if(found){
            arq.seek(arq.getFilePointer() - baAntigo.length);
            arq.write(baNovo);
         }
      }
      arq.close();
      return(true);
   }

   /*
   delete - deleta um registro
   @param int id
   @return boolean true (sucesso) false (falha)
   */
   public boolean delete(int id) throws Exception{
      //testar arquivo
      RandomAccessFile arq = new RandomAccessFile(this.file, "r");
      arq.seek(0);
      String nome = arq.readUTF();
      if (!nome.equals("CRUD"))
         throw new Exception("Arquivo !CRUD");
      arq.close();
      
      //abrir para escrita
      arq = new RandomAccessFile(this.file, "rw");
      
      //achar registro
      arq.seek(10);
      T objeto = this.constructor.newInstance();
      boolean found = false;

      //ler ate achar ou fim
      try{
         while(!found){
            byte lapide = arq.readByte();
            short tam = arq.readShort();
            byte[] ba = new byte[tam];
            arq.read(ba);
            objeto.fromByteArray(ba);
            if (lapide==0 && id==objeto.getID())
               found = true;
         }
      }
      catch(EOFException end){} 

      //verificar se registro foi encontrado
      if(found){
         byte[] ba = objeto.toByteArray();
         arq.seek(arq.getFilePointer() - (ba.length + 3));
         arq.writeByte(1);
      }
      //registro nao encontrado
      else
         throw new Exception("Registro !Encontrado");
      
      arq.close();
      return(true);
   }


}
