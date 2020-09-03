/*
Ciencia da Computacao - Pucminas
AED3 - manha
Pedro Henrique Lima Carvalho
651230
*/

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/*
Classe Livro
*/

class Livro implements Registro{

//atributos
   private int id;
   public String nome;
   public String autor;
   public float preco;
   private DecimalFormat df = new DecimalFormat("#,##0.00");

//contrutores
   public Livro(int i, String n, String a, float p){
      this.id = i;
      this.nome = n;
      this.autor = a;
      this.preco = p;
   }

   public Livro(){
      this.id = -1;
      this.nome = "vazio";
      this.autor = "vazio";
      this.preco = 0.0F;
   }

//metodos
   /*
   getID - retorna o ID de um objeto
   @return int id
   */
   public int getID(){
      return(this.id);
   }
   
   /*
   setID - atribui um ID para um objeto
   @param int n
   */
   public void setID(int n){
      this.id = n;
   }

   /*
   toByteArray - retorna o conteudo do objeto com byte[]
   @return byte[] ba
   */
   public byte[] toByteArray() throws IOException{
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);

      dos.writeInt(this.id);
      dos.writeUTF(this.nome);
      dos.writeUTF(this.autor);
      dos.writeFloat(this.preco);

      return(baos.toByteArray());
   }
      
   /*
   fromByteArray - preenche o objeto a partir de um byte[]
   @param byte[] ba
   */
   public void fromByteArray(byte[] ba) throws IOException{
      ByteArrayInputStream bais = new ByteArrayInputStream(ba);
      DataInputStream dis = new DataInputStream(bais);

      this.id = dis.readInt();
      this.nome = dis.readUTF();
      this.autor = dis.readUTF();
      this.preco = dis.readFloat();
   }

   /*
   toString - forma uma String a partir dos dados do objeto
   @return String livro
   */
   public String toString(){
      String resp = "\nID: "+this.id;
      resp = resp + "\nNome: "+this.nome;
      resp = resp + "\nAutor: "+this.autor;
      resp = resp + "\nPreço: R$"+(df.format(this.preco)+"\n");
      return(resp);
   }

}
