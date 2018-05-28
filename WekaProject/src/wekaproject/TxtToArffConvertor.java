
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wekaproject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
/**
 *
 * @author zubif_000
 */
public class TxtToArffConvertor {

    /**
     * @param args the command line arguments
     */
    
    public void Convert() throws IOException  {
        File SW_File = new File("C:\\Users\\k_hai\\Documents\\NetBeansProjects\\WekaProject\\src\\files\\stopwords.txt");
        List<String> swordArrayList = new ArrayList<String>();
        List<List<String>> docList = new ArrayList<List<String>>();
        List<List<Integer>> docFreqList = new ArrayList<List<Integer>>();
        List<Integer> docClassList = new ArrayList<Integer>();
        FileReader SW_fr=null;        
        try 
        {
            SW_fr =  new FileReader(SW_File.toString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TxtToArffConvertor.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br2 = new BufferedReader(SW_fr);
        String Rline2 =null;
        while ((Rline2 = br2.readLine() ) != null) 
        {       
            swordArrayList.add(Rline2);            
        }
        SW_fr.close();
        
        FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".txt");
            }
        };

        
        File folder_dest = new File("C:\\Users\\k_hai\\Documents\\NetBeansProjects\\WekaProject\\src\\files");
        File folder = new File("C:\\Data\\MSIT\\Information Retrieval\\Project\\Health-News-Tweets\\Health-Tweets");
        File[] listOfFiles = folder.listFiles(filter);
        List<String> wordArrayList = new ArrayList<String>();
        List<String> subDocList = new ArrayList<String>();
        List<Integer> subDocFreqList = new ArrayList<Integer>();
        
        for (int i = 0; i < listOfFiles.length; i++) 
        {
            //System.out.println("File#: "+i);
            File file = listOfFiles[i];
            File file_dest = new File (folder_dest.toString()+"\\"+file.getName());
            int numOfLines = countLinesNew(file.toString());
            int trainLines = (numOfLines*20)/100;
            PrintWriter writer = new PrintWriter(file_dest.toString(), "UTF-8");
            FileReader fr = null;
            try {
                fr =  new FileReader(file.toString());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TxtToArffConvertor.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader br = new BufferedReader(fr);
            String Rline = null;
            while ((Rline = br.readLine() ) != null) 
            {
                try 
                {
                    String line[] = Rline.replace("|", "=").split("=");
                    String line2[] = line[2].split("http");
                    WordTokenizer wordTokenizer = new WordTokenizer();
                    wordTokenizer.tokenize(line2[0].replaceAll("[^a-zA-Z\\s]", ""));
                    while(wordTokenizer.hasMoreElements()) 
                    {
                        String word = wordTokenizer.nextElement();
                        String wordCompare = word.toLowerCase();
                        if(!swordArrayList.contains(wordCompare) && !wordCompare.contains("@") && !wordCompare.contains("#") )
                        {
                            if(trainLines > 0)
                            {
                                if(!wordArrayList.contains(word.toLowerCase()))
                                {
                                    wordArrayList.add(word.toLowerCase());
                                }
                                if(!subDocList.contains(word.toLowerCase()))
                                {
                                    subDocList.add(word.toLowerCase());
                                    subDocFreqList.add(1);
                                }
                                else
                                {
                                    subDocFreqList.set(subDocList.indexOf(word.toLowerCase()),(subDocFreqList.get(subDocList.indexOf(word.toLowerCase()))+1));
                                }
                            }
                            else if(trainLines <= 0 && trainLines >= -5)
                            {
                                if(!subDocList.contains(word.toLowerCase()))
                                {
                                    subDocList.add(word.toLowerCase());
                                    subDocFreqList.add(1);
                                }
                                else
                                {
                                    subDocFreqList.set(subDocList.indexOf(word.toLowerCase()),(subDocFreqList.get(subDocList.indexOf(word.toLowerCase()))+1));
                                }
                            }
                            writer.println(word.toLowerCase());
                        }
                    }
                    docList.add(new ArrayList<String>(subDocList));
                    docFreqList.add(new ArrayList<Integer>(subDocFreqList));
                    docClassList.add(i);
                    subDocFreqList.clear();
                    subDocList.clear();
                    trainLines--;
                    //System.out.println(":::::::::::::::::::::::::::::Train Lines: "+trainLines);
                    if(trainLines <-5)
                    {
                        break;
                    }
                } catch ( Exception ex) 
                {
                    Logger.getLogger(TxtToArffConvertor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            fr.close();
            writer.close();
            writer.flush();                
        }
        File arff_file = new File("C:\\Users\\k_hai\\Documents\\NetBeansProjects\\WekaProject\\src\\files\\data.arff");
        PrintWriter arff_writer = new PrintWriter(arff_file.toString(), "UTF-8");
        arff_writer.println("@relation health_care");
        arff_writer.println();
        for(int i=0 ; i< wordArrayList.size() ; i++)
        {
            arff_writer.println("@attribute "+wordArrayList.get(i)+" numeric");
        }
        arff_writer.println("@attribute myclass {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}");
        //arff_writer.println("@attribute myclass {bbchealth,cbchealth,cnnhealth,everydayhealth,foxnewshealth,gdnhealthcare,goodhealth,KaiserHealthNews,latimeshealth,msnhealthnews,NBChealth,nprhealth,nytimeshealth,reuters_health,usnewshealth,wsjhealth}");
        arff_writer.println();
        arff_writer.println("@data");
        for(int i=0 ; i< docList.size() ; i++)
        {
            for(int j=0 ; j< wordArrayList.size() ; j++)
            {
                if(docList.get(i).contains(wordArrayList.get(j)))
                {
                    arff_writer.print(docFreqList.get(i).get(docList.get(i).indexOf(wordArrayList.get(j)))+",");
                }
                else
                {
                    arff_writer.print("0,");
                }
            }
            arff_writer.print(""+docClassList.get(i));
            arff_writer.println();
        }
        arff_writer.close();
        arff_writer.flush();        
    }

public static int countLinesNew(String filename) throws IOException 
    {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i=0; i<1024;) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                //System.out.println(readChars);
                for (int i=0; i<readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }    
}
