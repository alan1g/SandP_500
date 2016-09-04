
import java.util.Random;
import java.util.Scanner;

public class minVolatilitySP500
{
	
	public static void main(String args[])
	{
		
////////READ IN DATA///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		 FileIO io = new FileIO();
		 String[] original = io.load("D:/stockMarketB.txt");
		 int numrows=original.length;
		 int numcols=original[0].split("\t").length;
		 double[][] arValues = new double[numrows][numcols];

		 for(int i=0;i<numrows;i++)
		 {
			 for(int j=0;j<numcols;j++)
			 {
				 arValues[i][j]=Double.parseDouble(original[i].split("\t")[j]);
			 }
		 }
		 
		 FileIO io2 = new FileIO();
		 String[] arNames = io2.load("D:/StockNames.txt");
		 
		 FileIO io3 = new FileIO();
		 String[] stockPrice =  io3.load("D:/StockPrice.txt");
		 
		 
/////////AN ARRAY TO STORE ALL STOCK PURCAHSES AND PRINT TO SCREEN for Excel/////////////////////////////////////////////////////////////////////////
		 
		 Array finalresult = new Array(465);
		 for(int i=0;i<arNames.length;i++)
		 {
			 Node object = new Node();
			 object.name = arNames[i].trim();
			 object.index = i; 
			 finalresult.insert(object);
		 }

/////////CALCULATE VOLATILITY OF EACH S&P 500////////////////////////////////////////////////////////////////////////////////////////////
		
		 Array result = new Array(465);//Array to store each companies data of the S&P 500
		 double total = 0;
		 double total2 = 0;
		 double mean = 0;
		 double volatility= 0;

		 for(int j=0;j<numcols;j++)
		 {
			 //find mean
			 total =0;
			 for(int i=0; i<numrows;i++)
			 {
				 total += arValues[i][j];//add up all values in each column	 
			 }//end of first inner loop
			 
			 mean = total/(double)numrows;
			 
			 //find Standard Deviation
			 total2 =0;
			 for(int i=0;i<numrows;i++)
			 {
				 total2 += Math.pow((arValues[i][j] - mean),2);//SD formula
			 }//end of second inner loop
			 
			 //find volatility
			 volatility = Math.sqrt(total2/(double)numrows);
			 
			 Node object = new Node();//create new node and insert into Quick sort array
			 object.name = arNames[j].trim();
			 object.stockPrice = Integer.parseInt(stockPrice[j].trim());
			 object.volativity = volatility;
			 object.index = j;
			 result.insert(object);
			 
		 }//end of outer loop
		 
		 result.quickSort();
		 
/////////RANDOMIZE STOCK PURCHASES//////////////////////////////////////////////////////////////////////////////////////////////////
		 
		 //Ask user to enter amount to invest
		 Scanner scan = new Scanner(System.in);
		 System.out.println("Enter the amount to invest");
		 int amountToInvest = scan.nextInt();
		 
		 //Enter here how many top S&P 500 companies you wish to invest in
		 int topCompanies = 20;
		 
		 //Create an array of the top companies with lowest volatility
		 Array storeResult = new Array(topCompanies);
		 Array portfolioResult = new Array(topCompanies);
		 
		 //Choose top companies with a combination of low volatility results
		 
		 for(int i=1;i<topCompanies+1;i++)
		 {
			 Node object = new Node();
			 object.index = result.theArray[i].index;
			 object.name = result.theArray[i].name;
			 object.numOfStocks = result.theArray[i].numOfStocks;
			 object.stockPrice = result.theArray[i].stockPrice;
			 object.volativity = result.theArray[i].volativity;
			 storeResult.insert(object);
			 portfolioResult.insert(object);
			 
		 }
		 
		 
/////////Allocate amounts for each package of the portfolio ////////////////////////////////////////////////////////////////////////////////////////////////////
		 
		 
		 int percentage [] = new int[8]; 
		 percentage[0] = (amountToInvest/100)*12;
		 percentage[1] = (amountToInvest/100)*12;
		 percentage[2] = (amountToInvest/100)*12;
		 percentage[3] = (amountToInvest/100)*12;
		 percentage[4] = (amountToInvest/100)*12;
		 percentage[5] = (amountToInvest/100)*12;
		 percentage[6] = (amountToInvest/100)*14;
		 percentage[7] = (amountToInvest/100)*14;
		 



		 
		 NapSack portfolio[] = new NapSack[8];//Create a NapSack portfolio
		 
		 int kitty = 0;//what's left over after buying a stock
		 int monteCarlo = 2000;//number of times to run simulator
		 int multiply;//randomly multiply how many stocks to purchase
		 
		 for(int i=0;i<percentage.length;i++)//start filling the portfolio
		 {
		 			 
			 double ar[] = new double[numrows];//%column
	
			 NapSackArray NapSackResults = new NapSackArray(monteCarlo);//Create a package
			 Random r = new Random();
			 for(int k=0;k<monteCarlo;k++)//start monte carlo
			 {
				 kitty = percentage[i];//initialize kitty to be investment amount
				 while(kitty > 3000)//while kitty is greater than the lowest stock price of top Companies
				 {
					 	int num = (int)(Math.random()*topCompanies);//randomly choose company to invest in
						int check = storeResult.theArray[num].stockPrice;//check price of stock and if you can afford it, buy it.
						if(check<kitty)
						{
							storeResult.theArray[num].numOfStocks++;//increment number of stocks for that company
							kitty -= storeResult.theArray[num].stockPrice;//what's left in the kitty after purchase
						}
						
				 }
				 
				 //Create an array to store frequency distribution values of all purchased stocks
				 
				 int accumStockPrice =0;//variable to store total purchase
				 
				 //Calculate frequency distribution
				 for(int j=0;j<storeResult.theArray.length;j++)
				 {
					 if(storeResult.theArray[j].numOfStocks>0)
					 {
						 accumStockPrice += storeResult.theArray[j].numOfStocks*storeResult.theArray[j].stockPrice;
						 int index =0;
						 while(index<numrows)
						 {
							 ar[index] += arValues[index][storeResult.theArray[j].index]*(storeResult.theArray[j].stockPrice*storeResult.theArray[j].numOfStocks);
							 index++;
						 }
					 }
				 }
				 
				 //Calculate Standard Deviation from Change% column
				 total =0;
				 mean = 0;
				 for(int l=0;l<numrows;l++)
				 {
					 ar[l] = ar[l]/(double)accumStockPrice;
					 total =+ ar[l];
				 }
				 mean = total/(double)numrows;
				 total2 =0;
				 for(int m=0;m<numrows;m++)
				 {
					 total2 += Math.pow((ar[m] - mean),2);
				 }
				 volatility = Math.sqrt(total2/(double)numrows);
				 
				 //create new NapSack object and insert into Quick Sort Array
				 NapSack object = new NapSack(topCompanies);
				 object.minVolatility = volatility;
				 object.totalPurchase = accumStockPrice;
				 for(int s=0;s<topCompanies;s++)
				 {
					 Node nodeObject = new Node();
					 nodeObject.index = storeResult.theArray[s].index;
					 nodeObject.name = storeResult.theArray[s].name;
					 nodeObject.numOfStocks = storeResult.theArray[s].numOfStocks;
					 nodeObject.stockPrice = storeResult.theArray[s].stockPrice;
					 nodeObject.volativity = storeResult.theArray[s].volativity;
					 object.array[s]=nodeObject;
					 
				 }
				 NapSackResults.insert(object);
				 reset(storeResult);//reset storeResult numOfStocks to 0.
				 reset(ar);
			 }
			 
			 //sort the NapSack array and insert the lowest Nap Sack volatility to portfolio
			 NapSackResults.quickSort();
			 NapSack object = new NapSack(topCompanies);
			 object = NapSackResults.theArray[0];
			 portfolio[i] = object;
		 
		}
		
		for(int i =0;i<portfolio.length;i++)//print each package of the portfolio to screen
		{
			System.out.println("Package: "+(i+1));
			for(int j=0;j<portfolio[i].array.length;j++)
			{
				System.out.print("Index: "+portfolio[i].array[j].index+" Co. Name:"+portfolio[i].array[j].name.trim()+" Stock Price: "+portfolio[i].array[j].stockPrice+" Volativity: "+portfolio[i].array[j].volativity+" Number of stocks: "+portfolio[i].array[j].numOfStocks);// for each element,
				System.out.println();
			}
			System.out.println("Volatility: "+portfolio[i].minVolatility);
			System.out.println("Total Purchase: "+portfolio[i].totalPurchase);
			System.out.println();
		}
		
		
		for(int i=0;i<portfolio.length;i++)//transfer the data to the portfolio result
		{
			for(int j=0;j<topCompanies;j++)
			{
				portfolioResult.theArray[j].numOfStocks+=portfolio[i].array[j].numOfStocks;
			}
		}
		System.out.println("Portfolio Result");
		portfolioResult.display();
		
		double ar[] = new double[numrows];//Portfolio %column

		int accumStockPrice =0;//variable to store total purchase
		 
		 //Calculate frequency distribution of portfolio
		 for(int j=0;j<portfolioResult.theArray.length;j++)
		 {
			 if(portfolioResult.theArray[j].numOfStocks>0)
			 {
				 accumStockPrice += portfolioResult.theArray[j].numOfStocks*portfolioResult.theArray[j].stockPrice;
				 int index =0;
				 while(index<numrows)
				 {
					 ar[index] += arValues[index][portfolioResult.theArray[j].index]*(portfolioResult.theArray[j].stockPrice*portfolioResult.theArray[j].numOfStocks);
					 index++;
				 }
			 }
		 }
		 
		 //Calculate Standard Deviation from portfolio Change% column
		 total =0;
		 mean = 0;
		 for(int l=0;l<numrows;l++)
		 {
			 ar[l] = ar[l]/(double)accumStockPrice;
			 total =+ ar[l];
		 }
		 mean = total/(double)numrows;
		 total2 =0;
		 for(int m=0;m<numrows;m++)
		 {
			 total2 += Math.pow((ar[m] - mean),2);
		 }
		 volatility = Math.sqrt(total2/(double)numrows);
		 System.out.println("Portfolio Volatility: "+volatility);
		 System.out.println("Total Purchase: "+accumStockPrice);
		 scan.close();
		 
		 
		for(int i=0;i<topCompanies;i++)//transfer data from portfolio result to final result. This is used to print string number of stocks to screen
		{
			finalresult.theArray[portfolioResult.theArray[i].index].numOfStocks=portfolioResult.theArray[i].numOfStocks;
		}
		
		System.out.println("String of Stock Quantities (Excel):");
		finalresult.displayResults();
		
	}
	public static void reset(Array ar)//reset column% array stocks to screen
	{
		for(int i=0;i<ar.theArray.length;i++)
		{
			ar.theArray[i].numOfStocks=0;
			
		}
	}
	public static void reset(double ar[])
	{
		for(int i=0;i<ar.length;i++)
		{
			ar[i] = 0;
		}
	}
}
class Node
{
	String name;
	int stockPrice;
	double volativity;
	int numOfStocks = 0;
	int index;
}

class NapSack
{
	double minVolatility;
	int totalPurchase;
	Node array[];
	
	public NapSack(int num)
	{
		array = new Node[num];
	}
}

//Quick Sort Array of type NapSack
class NapSackArray
{
	public NapSack theArray[];
	private int nElems;
	
	public NapSackArray(int max)          
    {
    theArray = new NapSack[max];     
    nElems = 0;                   
    }
	
	public void insert(NapSack value)   
    {
    theArray[nElems] = value;      
    nElems++;                     
    }
	
	public void display()             
    {
		for(int k=0;k<theArray[0].array.length;k++)
		{
			System.out.print("Index: "+theArray[0].array[k].index+" Co. Name:"+theArray[0].array[k].name.trim()+" Stock Price: "+theArray[0].array[k].stockPrice+" Volativity: "+theArray[0].array[k].volativity+" Number of stocks: "+theArray[0].array[k].numOfStocks);// for each element,
			System.out.println();
		}
		System.out.println(); 
		System.out.println("Volatility: "+theArray[0].minVolatility);
		System.out.println("Total Purchase: "+theArray[0].totalPurchase);
    }
	
	public void displayResults()
	{
		
	}
	
	public void quickSort()
    {
		recQuickSort(0, nElems-1);
    }
	
	public void recQuickSort(int left, int right)
    {
    if(right-left <= 0)             
        return;                      
    else                            
       {
       NapSack pivot = theArray[right];     
                                        
       int partition = partitionIt(left, right, pivot);
       recQuickSort(left, partition-1);   
       recQuickSort(partition+1, right);  
       }
    }  // end recQuickSort()
	
	public int partitionIt(int left, int right, NapSack pivot)
     {
     int leftPtr = left-1;          
     int rightPtr = right;          
     while(true)
        {                           
        while( theArray[++leftPtr].minVolatility<pivot.minVolatility)
           ;  // (nop)
                                    
        while(rightPtr > 0 && theArray[--rightPtr].minVolatility>pivot.minVolatility)
           ;  // (nop)

        if(leftPtr >= rightPtr)      
           break;                    
        else                        
           swap(leftPtr, rightPtr); 
        }  // end while(true)
     swap(leftPtr, right);           
     return leftPtr;                 
     }  // end partitionIt()
	
	public void swap(int dex1, int dex2) 
    {
    NapSack temp = theArray[dex1];        
    theArray[dex1] = theArray[dex2]; 
    theArray[dex2] = temp;            
    }  // end swap(
	
}

//QuickSort class of type Node
class Array
{
	public Node theArray[];
	private int nElems;
	
	public Array(int max)          
    {
    theArray = new Node[max];      
    nElems = 0;                    
    }
	
	public void insert(Node value)    
    {
    theArray[nElems] = value;      
    nElems++;                    
    }

	public void display()             
    {
    
    for(int j=0; j<nElems; j++)
    {
    	System.out.print("Index: "+theArray[j].index+" Co. Name:"+theArray[j].name.trim()+" Stock Price: "+theArray[j].stockPrice+" Volativity: "+theArray[j].volativity+" Number of stocks: "+theArray[j].numOfStocks);// for each element,
    	System.out.println();
    }
       
    System.out.println();
    }
	public void displayResults()
	{
		for(int i=0;i<nElems;i++)
		{
			System.out.print(theArray[i].numOfStocks+",");
		}
		System.out.println();
	}

	public void quickSort()
    {
    recQuickSort(0, nElems-1);
    }

	public void recQuickSort(int left, int right)
    {
    if(right-left <= 0)              
        return;                    
    else                             
       {
       Node pivot = theArray[right];      
                                          
       int partition = partitionIt(left, right, pivot);
       recQuickSort(left, partition-1);   
       recQuickSort(partition+1, right); 
       }
    }  // end recQuickSort()

	public int partitionIt(int left, int right, Node pivot)
     {
     int leftPtr = left-1;         
     int rightPtr = right;          
     while(true)
        {                          
        while( theArray[++leftPtr].volativity<pivot.volativity)
           ;  
                                     
        while(rightPtr > 0 && theArray[--rightPtr].volativity>pivot.volativity)
           ;  

        if(leftPtr >= rightPtr)     
           break;                   
        else                         
           swap(leftPtr, rightPtr);  
        }  // end while(true)
     swap(leftPtr, right);          
     return leftPtr;                
     }  // end partitionIt()

	public void swap(int dex1, int dex2)  
    {
    Node temp = theArray[dex1];        
    theArray[dex1] = theArray[dex2];  
    theArray[dex2] = temp;             
    }  // end swap

}


