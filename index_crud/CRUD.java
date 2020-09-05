/*
Classe CRUD - Realiza Operacoes CRUD de Arquivo
Versão 2.0 - Arquivos Indexados
Cabecalho
   byte 0-8 tipo do arquivo "CRUD2.0"
   byte 9-12 proximo id (int) inicio em 1
Registros comecam byte 13
   lapide - 1 byte
   indicador de tamanho - 1 short
   conteudo = byte[]

*/
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.lang.reflect.Constructor;
import java.io.RandomAccessFile;
import java.io.File;
import aed3.*;

class CRUD <T extends Registro>{
//atributos da classe
   private static final long NEXT_ID = 9L;
   private static final String NAME_VERSION = "CRUD2.0";

//atributos
   private Constructor<T> constructor; 
   private RandomAccessFile arq;
   private HashExtensivel direto;
   private ArvoreBMais_String_Int indireto;

//construtor
   CRUD(Constructor<T> constructor, String file) throws Exception{
      //Construtor
      this.constructor = constructor;

      //Verificar se arquivo de dados com nome ja existe
      if (new File(file).exists()){
         
         //Verifica se o arquivo existente do tipo NAME_VERSION
         arq = new RandomAccessFile (file, "rws");
         arq.seek(0L);
         String nome = arq.readUTF();
         if (!nome.equals(NAME_VERSION))
            throw new Exception("Arquivo !CRUD2.0");
         
      }

      //Se nao exisitr criar
      else{ 
         arq = new RandomAccessFile (file, "rws");
         
         //Escreve NAME_VERSION no cabecalho para identificar o tipo do arquivo
         arq.writeUTF(NAME_VERSION);
         
         //Escreve o proximo id do arquivo
         arq.writeInt(1);
      }
      
      //criacao dos indices direto e indireto
      direto = new HashExtensivel(4, (file+".diretorio"), (file+".cestos"));
      indireto = new ArvoreBMais_String_Int(5, (file+".arvore"));
   }

//metodos
   /*
   create - Cria um registro no arquivo
   @param T objeto
   @return int id
   */
   public int create(T objeto) throws Exception{      
      //ler proximo id
      arq.seek(NEXT_ID);
      final int nextId = arq.readInt();
      
      //atualizar id no objeto
      objeto.setID(nextId);

      //Dados para escrita
      byte lapide = 0;
      byte[] ba = objeto.toByteArray();
      short tam = (short)ba.length;
      
      //escrever proximo ID
      arq.seek(NEXT_ID);
      arq.writeInt(nextId + 1);

      //escrever novo registro
      long pos = arq.length();
      arq.seek(pos);
      arq.writeByte(lapide);
      arq.writeShort(tam);
      arq.write(ba);
      
      //escrever indice direto
      direto.create(nextId, pos);

      //escrever indice indireto
      indireto.create(objeto.chaveSecundaria(), nextId);

      return(nextId);
   }
   
   /*
   read - Ler um objeto do arquivo
   @param int id
   @return T objeto
   */
   public T read(int id) throws Exception{ 
      //variaveis
      T objeto = this.constructor.newInstance();

      //ler posicao do indice direto
      long pos = direto.read(id);    
      //posicao encontrada
      if(pos != -1){
         arq.seek(pos);
         byte lapide = arq.readByte();
         short tam = arq.readShort();
         byte[] ba = new byte[tam];
         arq.read(ba);
         objeto.fromByteArray(ba);
         
         //verificar registro valido
         if(lapide !=0)
            objeto = null;
      }
      
      //posicao nao encontrada
      else{
         objeto = null;
      }

      return(objeto);
   }

   /*
   read - Ler um objeto do arquivo
   @param String chaveSecundaria
   @return T objeto
   */
   public T read(String chave) throws Exception{
      int id = indireto.read(chave);
      return (read(id));
   }
   
   /*
   update - atualiza um registro
   @param T objeto
   @return boolean true (sucesso) false (falha)
   */
   public boolean update(T objeto) throws Exception{ 
      //id objeto
      final int id = objeto.getID();
      
      //posicao do objeto no arquivo
      long pos = direto.read(id);

      //obter tamanho dos dados do objeto
      arq.seek(pos+1);
      short tam = arq.readShort();

      //objeto novo
      byte[] baNovo = objeto.toByteArray();
      short tamNovo = (short) baNovo.length;
   
      //novo registro maior que o anterior
      if(tam < tamNovo){
         //marcar arquivo como deletado
         arq.seek(pos);
         arq.writeByte(1);
         
         //criar novo registro
         pos = arq.length();
         arq.seek(pos);
         arq.writeByte(0);
         arq.writeShort(tamNovo);
         arq.write(baNovo);
         direto.update(id, pos);
         indireto.update(objeto.chaveSecundaria(), id);
      }      
      
      //novo registro menor ou igual ao anterior
      else{
         arq.seek(pos+3);
         arq.write(baNovo);
         indireto.update(objeto.chaveSecundaria(), id);
      }
      return(true);
   }

   /*
   delete - deleta um registro
   @param int id
   @return boolean true (sucesso) false (falha)
   */
   public boolean delete(int id) throws Exception{  
      //achar registro
      long pos = direto.read(id);
      arq.seek(pos);

      //carregar para objeto 
      T objeto = this.constructor.newInstance(); 
      byte lapide = arq.readByte();
      short tam = arq.readShort();
      byte[] ba = new byte[tam];
      arq.read(ba);
      objeto.fromByteArray(ba);
      
      //verificar se registro ja esta apagado
      if(lapide != 0)
         throw new Exception("Registro inexistente");
      else{
         
         //apagar registro
         arq.seek(pos);
         arq.writeByte(1);
         
         //apagar indice direto
         direto.delete(id);
         indireto.delete(objeto.chaveSecundaria());
      } 
      return(true);
   }
}
