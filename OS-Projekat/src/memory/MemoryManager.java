import java.util.ArrayList;
//import kernel.Process;
public class MemoryManager 
{
    public static ArrayList<PartitionMemory> partitionsInRam;
    public MemoryManager()
    {
        Ram.initialize();
        PartitionMemory.initialize(); 
        partitionsInRam=new ArrayList<>();

    }
    public int loadProcess(Process process)
    {   
        PartitionMemory partitionMemory=PartitionMemory.getPartitionByProcess(process);
        if(partitionMemory==null) //provjerava da li je već korišten taj proces
            return loadPartition(new PartitionMemory(process));
        else
            return loadPartition(partitionMemory);
    }
   
    public int loadPartition(PartitionMemory partiton) 
    {
        int position=load(partiton.getData());//učita podatke i vrati poziciju
        if(position!=-1)
        {
            partiton.setPositionInMemory(position);
            partitionsInRam.add(partiton);
            return position;
        }
        return position;
        
    }
    public int[] readProcess(Process process)
    {
        return readPartiton(PartitionMemory.getPartitionByProcess(process));
    }
    public int[] readPartiton(PartitionMemory partition)
    {
        if(partitionsInRam.contains(partition))
            return readRam(partition.getPositionInMemory(), partition.getSize());
        return null;
    }
    
    public int[] readRam(int start, int size)
    {
        int[] data=new int[size];
        for (int i = 0; i < data.length; i++) 
        {
            if(Ram.isOcupied(i+start))
            {
                data[i]=Ram.getAt(i+start);
            }
        }
        return data;
    }
    public boolean removeProcess(Process process)
    {
        return removePartition(PartitionMemory.getPartitionByProcess(process));
    }
    public boolean removePartition(PartitionMemory partition) 
    {
        if(partitionsInRam.contains(partition))
        {
            Ram.removeSequence(partition.getPositionInMemory(),partition.getSize());
            partitionsInRam.remove(partition);
            partition.setPositionInMemory(-1);
            
        }
        return false;
    }
    
   
    private int load(int[] data) //učitava podatke u RAM po pricipu najbolje odgovarajuće particije
    {
        int size=data.length;
        if(size>Ram.getFreeSpace()) //ako nema mjesta vraća -1
        {
            return -1;
        }
        int bestPosition=-1, bestFitSize=Integer.MAX_VALUE;
        int currentPosition=-1, currentSize=0; 
        for(int i=0;i<Ram.getCapacity();i++)
        {
            if(Ram.isOcupied(i) && currentSize!=0)//ako poslije praznih naiđe na zauzeto
            {
                if(currentSize>=size && currentSize<bestFitSize)//ako je najbolje do sad
                {
                    bestFitSize=currentSize;
                    bestPosition=currentPosition;
                }
                currentPosition=-1;
                currentSize=0;
            }
            else if(!Ram.isOcupied(i) && currentSize==0) //kad naiđe na prvo slobodno poslije zauzetih
            {                    
                currentPosition=i;
                currentSize=1;
            }
            else if(!Ram.isOcupied(i) && currentSize!=0)//broji slobodna mjesta
            {
                currentSize++;
            }
            
            
        }
        if(currentSize>=size && currentSize<bestFitSize) //provjerava da li je posljednji blok najbolji
        {
            bestFitSize=currentSize;
            bestPosition=currentPosition;
        }
        if(bestPosition==-1) // ako ne pronađe nijedan blok da odgovara
        {
            defragmentation();  //sažimanje
            bestPosition=Ram.getOccupiedSpace();//kada su podaci sažeti prva slobodna pozicija je na kraju
        }

        if(Ram.setSequence(bestPosition, data))//postavlja podatke na najbolju poziciju
            return bestPosition;

        return -1; //Greška
    }
    private void defragmentation() 
    {
        int freePosition=-1;
        boolean avaliablePosition=false;
        for(int i=0; i<Ram.getCapacity();i++)
        {
            if(!Ram.isOcupied(i) && !avaliablePosition)//pronalazi prvu slobodnu poziciju
            {
                freePosition=i;
                avaliablePosition=true;
            }
            else if(Ram.isOcupied(i) && avaliablePosition)//prva zauzeta pozicija poslije slobodnih
            {
                PartitionMemory partition=PartitionMemory.getPartitionByAddress(i);//pronalazi particiju na poziciji i
                int size=partition.getSize();
                int j;
                for (j = freePosition; j < freePosition+size; j++) 
                {
                    Ram.setAt(j, Ram.getAt(i));//postavlja na j sa zauzetog dijela koji je na i
                    Ram.removeAt(i); //uklanja sa i
                    i++;
                }
                partition.setPositionInMemory(freePosition);
                i=j-1; //vraća i na kraj nove pozicije i smanjuje za jedan jer će u for petlji biti i++
                avaliablePosition=false;
                
            }
        }
    }
    public static ArrayList<PartitionMemory> getPartitionsInRam() {
        return partitionsInRam;
    }
}