package finmngr;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import java.time.*;
import java.net.URL;
import java.nio.file.*;
import javax.swing.Timer;
import java.math.BigInteger;

/**
 * Arya Kondur
 * Project Start: May 27, 2020
 */

public class FinMngr extends JFrame {

    JFrame frame;
    Container ctr;
    CardLayout card;
    WelcomePanel start;
    ProfilePanel pp;
    AddRemCategoryPanel acp;
    ExpRepPanel erp;
    ExpensePanel ep;
    VendorPanel vp;
    EditCategoryPanel sbp;
    ViewSummaryPanel vsp;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;
    ArrayList<Character> validChars = new ArrayList<Character>();
    ArrayList<String> allUsernames = new ArrayList<String>();
    ArrayList<String> allPasswords = new ArrayList<String>();
    String currentUsername;
    String[] monthsList = new String[] {"Month", "January", "February", "March", "April",
        "May", "June", "July", "August", "September", "October", "November", "December"};
    ArrayList<Category> userCategories = new ArrayList<Category>();
    ArrayList<String> userVendors = new ArrayList<String>();
    String mainFolderName = "FinMngr Docs";
    ArrayList<ArrayList<String>> entriesList = new ArrayList<ArrayList<String>>(); // list of all expense entries
    Image qMark;
    
    public FinMngr()
    {
        // initializes frame with title "Personal Finance Manager"
        frame = new JFrame("Personal Finance Manager"); 
        ctr = this.getContentPane(); // initializes Container ctr
        frame.setSize(width, height); // set size of frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true); // allows frame to be seen
        card = new CardLayout(); // initializes CardLayout
        ctr.setLayout(card); // sets layout of container to card layout
        frame.add(ctr); // add container to frame
        
        createMainFolder();
        readInfoFile();
        initImages();
        qMark = new ImageIcon(mainFolderName + File.separator + "questionMark.png").getImage();
        
        //frame.addWindowListener(this);
        
        fillValidChars();
        
        start = new WelcomePanel();
        ctr.add(start, "Welcome Panel");
        
        CrtAcctPanel cap = new CrtAcctPanel();
        ctr.add(cap, "Create Account Panel");
        
        pp = new ProfilePanel();
        ctr.add(pp, "Profile Panel");
        
        ep = new ExpensePanel();
        ctr.add(ep, "Expense Panel");
        
        acp = new AddRemCategoryPanel();
        ctr.add(acp, "AddRem Category Panel");
        
        erp = new ExpRepPanel();
        JScrollPane js = new JScrollPane(erp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        js.getVerticalScrollBar().setUnitIncrement(20); // scrollbar speed
        ctr.add(js, "ExpRep Panel");
        
        vp = new VendorPanel();
        ctr.add(vp, "Vendor Panel");
        
        sbp = new EditCategoryPanel();
        ctr.add(sbp, "Edit Category Panel");
        
        vsp = new ViewSummaryPanel();
        JScrollPane js2 = new JScrollPane(vsp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        js2.getVerticalScrollBar().setUnitIncrement(24); // scrollbar speed
        js2.getHorizontalScrollBar().setUnitIncrement(24); // scrollbar speed
        ctr.add(js2, "View Summary Panel");
    } 
    
    public static void main(String[] args) {
        FinMngr fm = new FinMngr();
    }
    
    public void createMainFolder()
    {
        File main = new File(mainFolderName);
        main.mkdir();
    }
    
    /**
     * Fills validChars ArrayList with valid characters for passwords
     * Valid characters include any character with ASCII code between 33 and 126 (inclusive)
     */
    public void fillValidChars()
    {
        for (int i = 33; i < 127; i++)
            validChars.add((char)(i));
    }
    
    /**
     * Fills the userVendors ArrayList using information from given File
     * @param fileName      Name of file to read
     */
    public void fillVendorsList(String fileName)
    {
        String folderName = currentUsername + " - Info";
        File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
        Scanner scan = null;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                userVendors.add(scan.nextLine());
            }
            ascendingMergeSortForLists(userVendors);
        } catch (Exception e) {
            // If file not found, that means there are no categories created
        }
    }
    
    /**
     * Fills the entriesList ArrayList using information from given File
     * @param fileName      Name of file to read
     */
    public void fillEntriesList(String fileName)
    {
        String folderName = currentUsername + " - Info";
        File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
        Scanner scan = null;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] arr = line.split(",");
                ArrayList<String> toAdd = new ArrayList<String>(5);
                toAdd.add(arr[0]); toAdd.add(arr[1]); toAdd.add(arr[2]);
                toAdd.add(arr[3]); toAdd.add(arr[4]);
                entriesList.add(toAdd);
            }
        } catch (Exception e) {
            // If file not found, that means there are no categories created
        }
        sortEntriesList(entriesList);
    }
    
    /**
     * Add given vendor to appropriate vendors list (.txt file)
     * @param vendor    Vendor given
     */
    public void appendVendor(String vendor)
    {
        String folderName = currentUsername + " - Info";
        String fileName = "vendors - " + currentUsername + ".txt";
        File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(file, true);
            pw = new PrintWriter(fw);
            vendor = vendor.toLowerCase();
            if (!userVendors.contains(vendor))
                pw.println(vendor);
        } catch (Exception e) {
            System.err.println("Error with File Writing");
            System.exit(9);
        }
        pw.close();
    }
    
    /**
     * Sorts given ArrayList in ascending order using Merge Sort
     * @param list          List to sort
     */
    public void ascendingMergeSortForLists(ArrayList<String> list)
    {
        // if list.size() is 0 or 1, it is already sorted
        if (list.size() > 1) {
           int mid = list.size()/2;
            ArrayList<String> arr1 = new ArrayList<String>();
            for (int i = 0; i < mid; i++)
                arr1.add(list.get(i));
            ArrayList<String> arr2 = new ArrayList<String>();
            for (int i = mid; i < list.size(); i++)
                arr2.add(list.get(i));
            ascendingMergeSortForLists(arr1);
            ascendingMergeSortForLists(arr2);
            
            int left = 0;
            int right = 0;
            int arrCount = 0;
            
            while (left < arr1.size() && right < arr2.size()) {
                if (arr1.get(left).compareTo(arr2.get(right)) < 0) {
                    list.set(arrCount, arr1.get(left));
                    left++;
                    arrCount++;
                } else {
                    list.set(arrCount, arr2.get(right));
                    right++;
                    arrCount++;
                }      
                
                // if index is at last value of 1st half list, add all values from 2nd half list
                if (left == arr1.size()) {
                    while (right < arr2.size()) {
                        list.set(arrCount, arr2.get(right));
                        arrCount++;
                        right++;
                    }
                }
                // if index is at last value of 2nd half list, add all values from 1st half list
                if (right == arr2.size()) {
                    while (left < arr1.size()) {
                        list.set(arrCount, arr1.get(left));
                        arrCount++;
                        left++;
                    }
                }
            }
        }
    }
    
    /**
     * Sorts given ArrayList in ascending order using Merge Sort
     * Mainly used for sorting userCategories ArrayList
     * @param list          List to sort
     */
    public void sortCategoriesList(ArrayList<Category> list)
    {
        // if list.size() is 0 or 1, it is already sorted
        if (list.size() > 1) {
           int mid = list.size()/2;
            ArrayList<Category> arr1 = new ArrayList<Category>();
            for (int i = 0; i < mid; i++)
                arr1.add(list.get(i));
            ArrayList<Category> arr2 = new ArrayList<Category>();
            for (int i = mid; i < list.size(); i++)
                arr2.add(list.get(i));
            sortCategoriesList(arr1);
            sortCategoriesList(arr2);
            
            int left = 0;
            int right = 0;
            int arrCount = 0;
            
            while (left < arr1.size() && right < arr2.size()) {
                String leftName = arr1.get(left).getName().toLowerCase();
                String rightName = arr2.get(right).getName().toLowerCase();
                if (leftName.compareTo(rightName) < 0) {
                    list.set(arrCount, arr1.get(left));
                    left++;
                    arrCount++;
                } else {
                    list.set(arrCount, arr2.get(right));
                    right++;
                    arrCount++;
                }      
                
                // if index is at last value of 1st half list, add all values from 2nd half list
                if (left == arr1.size()) {
                    while (right < arr2.size()) {
                        list.set(arrCount, arr2.get(right));
                        arrCount++;
                        right++;
                    }
                }
                // if index is at last value of 2nd half list, add all values from 1st half list
                if (right == arr2.size()) {
                    while (left < arr1.size()) {
                        list.set(arrCount, arr1.get(left));
                        arrCount++;
                        left++;
                    }
                }
            }
        }
    }
    
    /**
     * Sorts entriesList ArrayList according to date (oldest at the start of list)
     * Implementation of Merge Sort
     * @param list      List to sort
     */
    public void sortEntriesList(ArrayList<ArrayList<String>> list)
    {
        // if list.size() is 0 or 1, then list is already sorted
        if (list.size() > 1) {
            int mid = list.size()/2;
            ArrayList<ArrayList<String>> arr1 = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < mid; i++)
                arr1.add(list.get(i));
            ArrayList<ArrayList<String>> arr2 = new ArrayList<ArrayList<String>>();
            for (int i = mid; i < list.size(); i++)
                arr2.add(list.get(i));
            sortEntriesList(arr1);
            sortEntriesList(arr2);
            
            int left = 0;
            int right = 0;
            int arrCount = 0;
            
            while (left < arr1.size() && right < arr2.size()) {
                String date1 = arr1.get(left).get(0);
                String date2 = arr2.get(right).get(0);
                String[] date1Arr = date1.split("/");
                String[] date2Arr = date2.split("/");
                if (givenIsAfterToday(Integer.parseInt(date2Arr[0]), Integer.parseInt(date2Arr[1]),
                        Integer.parseInt(date2Arr[2]), Integer.parseInt(date1Arr[0]), 
                        Integer.parseInt(date1Arr[1]), Integer.parseInt(date1Arr[2]))) {
                    list.set(arrCount, arr1.get(left));
                    arrCount++;
                    left++;
                } else {
                    list.set(arrCount, arr2.get(right));
                    arrCount++;
                    right++;
                }
                // if index is at last value of 1st half list, add all values from 2nd half list
                if (left == arr1.size()) {
                    while (right < arr2.size()) {
                        list.set(arrCount, arr2.get(right));
                        arrCount++;
                        right++;
                    }
                }
                // if index is at last value of 2nd half list, add all values from 1st half list
                if (right == arr2.size()) {
                    while (left < arr1.size()) {
                        list.set(arrCount, arr1.get(left));
                        arrCount++;
                        left++;
                    }
                }
            }
        }
    }
    
    /**
     * Fills the userCategories ArrayList using information from given File
     * @param fileName      Name of file to read
     */
    public void fillCategoriesList(String fileName)
    {
        String folderName = currentUsername + " - Info";
        File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
        Scanner scan = null;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] arr = line.split(",");
                String catName = arr[0].trim();
                String catSymb = arr[1].trim();
                String catBudg = arr[2].trim();
                userCategories.add(new Category(catName, catSymb, catBudg));
            }
            sortCategoriesList(userCategories);
        } catch (Exception e) {
            // If file not found, that means there are no categories created
        }
    }
    
    /**
     * Updates the categories csv file following a removal
     * @param fileName      File name of csv file
     */
    public void rewriteCategories(String fileName)
    {
       String folderName = currentUsername + " - Info";
       File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
       FileWriter fw = null;
       PrintWriter pw = null;
       try {
           fw = new FileWriter(file);
           pw = new PrintWriter(fw);
           for (int i = 0; i < userCategories.size(); i++) {
               Category c = userCategories.get(i);
               pw.println(c.getName().trim()+","+c.getSymbol().trim()+","+c.getBudget());
           }                
       } catch (Exception e) {
           System.err.println("Error with File Writing");
           System.exit(7);
       }
       pw.close();
    }
    
    /**
     * Initialize and save the images needed for the app
     */
    public void initImages()
    {
        System.setProperty("http.agent", "Chrome");
        File dSignFile = new File(mainFolderName + File.separator + "dollarsign.gif");
        boolean dSignFileExists = dSignFile.exists();
        File profPicFile = new File(mainFolderName + File.separator + "profilePic.png");
        boolean profPicFileExists = profPicFile.exists();
        File qMarkFile = new File(mainFolderName + File.separator + "questionMark.png");
        boolean qMarkFileExists = qMarkFile.exists();
        try {
            if (!dSignFileExists) {
                InputStream in = new URL("https://bestanimations.com/Money/Dollars/dollar-sign-symbol-24.gif").openStream();
                Files.copy(in, Paths.get(mainFolderName+File.separator+"dollarsign.gif"));
                in.close();
            }
            if (!profPicFileExists) {
                InputStream in = new URL("https://img.pngio.com/deafult-profile-icon-png-image-free-download-searchpngcom-profile-icon-transparent-673_673.png").openStream();
                Files.copy(in, Paths.get(mainFolderName+File.separator+"profilepic.png")); 
                in.close();
            }
            if (!qMarkFileExists) {
                InputStream in = new URL("https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/Blue_question_mark_icon.svg/1200px-Blue_question_mark_icon.svg.png").openStream();
                Files.copy(in, Paths.get(mainFolderName+File.separator+"questionMark.png")); 
                in.close();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    /**
    * Reads the text file with all usernames and passwords
    * Fills the ArrayLists allUsernames and allPasswords
    */
    public void readInfoFile()
    {
        File file = new File(mainFolderName+File.separator+"User Information.txt");
        Scanner scan = null;
        try {
            scan = new Scanner(file);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] arr = line.split(" --- ");
                allUsernames.add(arr[0]);
                allPasswords.add(arr[1]);
            }
        } catch (Exception e) {
            // If file not found, that means there are no accounts created
        }
    }
    
    /**
    * Checks if given date is after today's date
    * @param m1        Given month
    * @param d1        Given day
    * @param y1        Given year
    * @param m2        Today's month
    * @param d2        Today's day
    * @param y2        Today's year
    * @return 
    */
    public boolean givenIsAfterToday(int m1, int d1, int y1, int m2, int d2, int y2)
    {
       if (y1 > y2) // given year is greater than today's year
           return true;
       if (y1 == y2 && m1 > m2) // years are equal but given month is greater
           return true;
       if (y1 == y2 && m1 == m2 && d1 > d2) // year/month equal but given day is greater
           return true;
       return false;
    }

    /**
     * Checks if the given year is a leap year
     * @param year      Year given
     * @return          True if leap year ... False if not a leap year
     */
    public boolean isLeapYear(int year)
    {
        if (year % 400 == 0)
            return true;
        if (year % 100 == 0)
            return false;
        if (year % 4 == 0)
            return true;
        return false;
    }
    
    /**
     * Shows the given msg when the Help Button is clicked
     * The msg variable is cut into bits of 50 chars at most and then shown
     * using a JOptionPane
     * @param msg       Message to display
     */
    public void showHelpMessage(String msg)
    {
        String msgToShow = "";
        int charCount = 0;
        for (int i = 0; i < msg.length(); i++) {
            charCount++;
            if (charCount == 50) { // max number of chars on one line is set to 50
                // if the current char is a space, add it because it will later be deleted
                if (msg.charAt(i) == ' ')
                    msgToShow += " ";
                int indexSpace = msgToShow.lastIndexOf(" ");
                // cut msgToShow to the last space
                msgToShow = msgToShow.substring(0, indexSpace) + "\n"; 
                // setting i to indexToSpace essentially starts the counter at the word before 50 characters was reached
                i = indexSpace;
                charCount = 0;
            } else {
                msgToShow += msg.charAt(i);
            }
        }
        JOptionPane.showMessageDialog(frame, msgToShow);
    }
    
    /**
    * Encrypts str using RSA algorithm
    * @param str       String to encrypt
    * @return          Encrypted string
    */
    public String encryptRSA(String str)
    {
       /* RSA Algorithm description: http://www.dwhsurveillancesolutions.com/n8.pdf
       There are 94 valid characters so when we do modulus we must have n
       be at least 94.
       Choose two prime numbers --> Here, we choose p = 2, q = 47
       Compute n = p*q and z = (p-1)(q-1) --> n = 94, z = 46
       Choose a number e < n such that e and z are coprime --> We choose e = 3
       Find a number d such that e*d / z has a remainder of 1 --> d = 31
       Public key is (n,e) & Private key = (n,d)
       Encrypted value C of plaintext M --> C = (M^e) mod n
       Decrypted value M of encrypted C --> M = (C^d) mod n
       */

       String toReturn = "";
       int n = 94; int d = 31; int e = 3;
       for (int i = 0; i < str.length(); i++) {
           char charVal = str.charAt(i);
           int code = validChars.indexOf(charVal);
           long val = 0L;
           val = (long)(Math.pow(code, e)) % n;
           toReturn += (val+" ");
       }
       return toReturn;
    }

    /**
    * Decrypts str using RSA algorithm
    * @param str       String to decrypt
    * @return          Decrypted string
    */
    public String decryptRSA(String str)
    {
       // see encryptRSA method for explanation
       String toReturn = "";
       BigInteger n = BigInteger.valueOf(94);
       int d = 31; int e = 3;
       String[] values = str.split(" ");
       for (int i = 0; i < values.length; i++) {
           long val = Long.parseLong(values[i]);
           BigInteger bi = BigInteger.valueOf(val);
           bi = bi.pow(d);
           bi = bi.mod(n);
           int index = bi.intValue();
           //int dec = (int)(Math.pow(val, d)) % n;
           toReturn += validChars.get(index);
       }
       return toReturn;
    }
    
    /**
     * Main screen for the app
     */
    class WelcomePanel extends JPanel implements ActionListener, MouseListener, KeyListener
    {
        JTextField enterName;
        JPasswordField enterPass;
        Timer textTimer, textTimer2, sign1Timer;
        
        int textTimerIter = 0;
        int textTimerIter2 = 0;
        //int sign1X, sign1Y;
        int qMark1X, qMark1Y, qMark2X, qMark2Y;
        int qMarkDiam;
        
        // Class needed for timer that makes welcome message show
        class TextMover implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
                String str = "Welcome to FinMngr!";
                String str2 = "Your personal finance manager";
                textTimerIter++;
                if (textTimerIter > str.length()) {
                    textTimer.stop();
                    textTimerIter--;
                    textTimer2.start();
                    textTimerIter2++;
                    if (textTimerIter2 > str2.length()) {
                        textTimer2.stop();
                        textTimerIter2--;
                    }
                }
                repaint();
            }
        }
        
        /*class SignMover implements ActionListener
        {
            int topLeftCornerCount = 0;
            public void actionPerformed(ActionEvent e)
            {
                if (sign1X >= 50 && sign1X < 350 && sign1Y == 100) {
                    sign1X++;
                } else if (sign1Y >= 100 && sign1Y < 400 && sign1X == 350) {
                    sign1Y++;
                } else if (sign1X > 50 && sign1X <= 350 && sign1Y == 400) {
                    sign1X--;
                } else if (sign1Y > 100 && sign1Y <= 400 && sign1X == 50) {
                    sign1Y--;
                }
                if (sign1X == 50 && sign1Y == 100)
                    topLeftCornerCount++;
                if (topLeftCornerCount == 8)
                    sign1Timer.stop();
            }
        }*/
        
        public WelcomePanel()
        {
            setLayout(null);
            addMouseListener(this);
            //addKeyListener(this);
            //sign1X = (int)(Math.random()*250)+50; sign1Y = 100;
            
            TextMover tm = new TextMover();
            textTimer = new Timer(100, tm);
            textTimer.start();
            textTimer2 = new Timer(75, tm);
            
            //SignMover sm = new SignMover();
            //sign1Timer = new Timer(10, sm);
            //sign1Timer.start();
            
            JButton loginButton = new JButton("Sign in");
            loginButton.addActionListener(this);
            loginButton.setBounds(width/2-50, height - 300, 100, 50);
            add(loginButton);
            
            JButton createButton = new JButton("Create Account");
            createButton.addActionListener(this);
            createButton.setBounds(width/2-100, height - 200, 200, 50);
            add(createButton);
            
            enterName = new JTextField();
            enterName.setBounds(width/2, 165, 200, 50);
            add(enterName);
            
            enterPass = new JPasswordField();
            enterPass.setBounds(width/2, 265, 200, 50);
            enterPass.addKeyListener(this);
            add(enterPass);
        }
        
        public void mouseClicked(MouseEvent e)
        {
            int clickX = e.getX();
            int clickY = e.getY();
            /*
            The top left coordinates for the username help button are (qMark1X, qMark1Y).
            Its center is (qMark1X+qMarkDiam/2, qMark1Y+qMarkDiam/2) and its
            radius = qMarkDiam/2. The equation for this can be written as
            (x-qMark1X-qMarkDiam/2)^2 + (y-qMark1Y-qMarkDiam/2)^2 = qMarkDiam^2. 
            If we substitute the values  clickX and clickY into the equation and 
            the result is <= qMarkDiam^2, the click occurred within the help button 
            and we must show the JOptionPane.
            */
            if (Math.pow(clickX-qMark1X-qMarkDiam/2, 2) + Math.pow(clickY-qMark1Y-qMarkDiam/2, 2) <= Math.pow(qMarkDiam, 2)) {
                String str = "Please enter your username here. If you have not created an account, your username is not in the system. You will be prompted to create a new account.";
                showHelpMessage(str);
            } else if (Math.pow(clickX-qMark2X-qMarkDiam/2, 2) + Math.pow(clickY-qMark2Y-qMarkDiam/2, 2) <= Math.pow(qMarkDiam, 2)) {
                String str = "Please enter your password here. If you enter an incorrect or invalid password, you will be notified as such.";
                showHelpMessage(str);
            } 
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            // if key typed is the enter button (KeyEvent.VK_ENTER)
            if (key == KeyEvent.VK_ENTER) {
                String username = enterName.getText();
                // getPassword() returns char[] --> use new String() to convert to String
                String password = new String(enterPass.getPassword());
                // verify that username and password are correct
                String verifMessage = verifyUser(username, password);
                
                if (verifMessage.equals("Correct Account")) {
                    currentUsername = username;
                    enterName.setText(""); username = "";
                    enterPass.setText(""); password = "";
                    fillCategoriesList("categories - "+currentUsername+".csv");
                    fillVendorsList("vendors - "+currentUsername+".txt");
                    /*pp.optionsBtn.removeAllItems();
                    pp.optionsBtn.addItem(currentUsername);
                    pp.optionsBtn.addItem("Change Password");
                    pp.optionsBtn.addItem("Help");
                    pp.optionsBtn.addItem("Sign Out");*/
                    card.show(ctr, "Profile Panel");
                } else if (verifMessage.equals("No name")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid username!");
                    repaint();
                } else if (verifMessage.equals("No pass")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid password!");
                    repaint();
                } else if (verifMessage.equals("Not reg")) {
                    JOptionPane.showMessageDialog(frame, "Account does not exist.\nPlease create a new account.");
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect password!");
                    repaint();
                }
            }
        }
        public void keyReleased(KeyEvent e) {}
        
        public void keyTyped(KeyEvent e) {}

        // actionPerformed method for WelcomePanel class
        public void actionPerformed (ActionEvent e)
        {
            String username = enterName.getText();
            // getPassword() returns char[] --> use new String() to convert to String
            String password = new String(enterPass.getPassword());
            // verify that username and password are correct
            String verifMessage = verifyUser(username, password);
            String command = e.getActionCommand();
            if (command.equals("Create Account")) {
                enterName.setText(""); username = "";
                enterPass.setText(""); password = "";
                card.show(ctr, "Create Account Panel");
            } else if (command.equals("Sign in") && verifMessage.equals("Correct Account")) {
                currentUsername = username;
                enterName.setText(""); username = "";
                enterPass.setText(""); password = "";
                fillCategoriesList("categories - "+currentUsername+".csv");
                fillVendorsList("vendors - "+currentUsername+".txt");
                /*pp.optionsBtn.removeAllItems();
                pp.optionsBtn.addItem(currentUsername);
                pp.optionsBtn.addItem("Change Password");
                pp.optionsBtn.addItem("Help");
                pp.optionsBtn.addItem("Sign Out");*/
                card.show(ctr, "Profile Panel");
            } else if (verifMessage.equals("No name")) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid username!");
                repaint();
            } else if (verifMessage.equals("No pass")) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid password!");
                repaint();
            } else if (verifMessage.equals("Not reg")) {
                JOptionPane.showMessageDialog(frame, "Account does not exist.\nPlease create a new account.");
                repaint();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect password!");
                repaint();
            }
        }
        /**
         * Checks if the given username and password are valid and correct
         * @param name      Entered username
         * @param pass      Entered password
         * @return          Associated message with the given username/password
         */
        public String verifyUser(String name, String pass)
        {
            if (name.replace(" ", "").length() == 0) 
                return "No name";
            if (pass.replace(" ", "").length() < 8) 
                return "No pass";
            int index = allUsernames.indexOf(name);
            if (index == -1) 
                return "Not reg";
            String encryptedPass = allPasswords.get(index);
            String decryptedPass = decryptRSA(encryptedPass);
            boolean passwordMatch = pass.equals(decryptedPass);
            if (!passwordMatch) 
                return "Wrong pass";
            return "Correct Account";
        }
        
        // paintComponent method for WelcomePanel class
        public void paintComponent (Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            // for loop creates white to black gradient for background
            for (int i = 0; i < height; i++) {
                g.setColor(new Color((int)(color - (i*255/height)), (int)(color - (i*255/height)), (int)(color - (i*255/height))));
                g.drawLine(0, i, width, i);
            }
            //g.setColor(new Color(35, 213, 255));
            /*for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            
            Image dollarSign = new ImageIcon(mainFolderName+File.separator+"dollarsign.gif").getImage();
            g.drawImage(dollarSign, 125, 300, 75, 200, this);
            g.drawImage(dollarSign, width-175, 300, 75, 200, this);
            //g.drawImage(dollarSign, sign1X, sign1Y, 75, 200, this);
            //g.drawImage(dollarSign, 400-sign1X, 100+400-sign1Y, 75, 200, this);
            //g.drawImage(dollarSign, 900+sign1X, sign1Y, 75, 200, this);
            //g.drawImage(dollarSign, 2200-(900+sign1X), 100+400-sign1Y, 75, 200, this);
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String welcomeMsg = "Welcome to FinMngr!";
            String msgToWrite1 = welcomeMsg.substring(0, textTimerIter);
            int strWidth = g.getFontMetrics().stringWidth(msgToWrite1);
            g.drawString(msgToWrite1, (width-strWidth)/2, 80);
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            String subHeader = "Your personal finance manager";
            String msgToWrite2 = subHeader.substring(0, textTimerIter2);
            strWidth = g.getFontMetrics().stringWidth(msgToWrite2);
            // color changes from white to green as text is written --> ending color is 0, 127, 0
            g.setColor(new Color(255-255*textTimerIter2/subHeader.length(), 255-255*textTimerIter2/subHeader.length()/2, 255-255*textTimerIter2/subHeader.length()));
            g.drawString(msgToWrite2, (width-strWidth)/2, 130);
            g.setColor(Color.BLACK);
            strWidth = g.getFontMetrics().stringWidth("Username: ");
            g.drawString("Username: ", width/2-strWidth, 200);
            qMark1X = width/2-strWidth-20-5; qMark1Y = 200-20; qMarkDiam = 20;
            g.drawImage(qMark, qMark1X, qMark1Y, qMarkDiam, qMarkDiam, this);
            strWidth = g.getFontMetrics().stringWidth("Password: ");
            g.drawString("Password: ", width/2-strWidth, 300);
            qMark2X = width/2-strWidth-20-5; qMark2Y = 300-20;
            g.drawImage(qMark, qMark2X, qMark2Y, qMarkDiam, qMarkDiam, this);
            //grabFocus();
        }
    }
    
    /**
     * Screen for creating an account
     */
    class CrtAcctPanel extends JPanel implements ActionListener, MouseListener
    {
        JTextField name, email, username;
        JPasswordField password, password2;
        /*
        2D array for qMark image positions
        array size: 5x2 (5 images needed, 2 coordinates --> x,y)
        */
        int[][] qMarkPos = new int[5][2]; 
        int qMarkDiam = 20;
        
        public CrtAcctPanel()
        {
            setLayout(null);
            addMouseListener(this);
            
            name = new JTextField();
            name.addActionListener(this);
            name.setBounds(width/2, 240, 200, 50);
            add(name);
            
            email = new JTextField();
            email.addActionListener(this);
            email.setBounds(width/2, 315, 200, 50);
            add(email);
            
            username = new JTextField();
            username.addActionListener(this);
            username.setBounds(width/2, 390, 200, 50);
            add(username);
            
            password = new JPasswordField();
            password.addActionListener(this);
            password.setBounds(width/2, 465, 200, 50);
            add(password);
            
            password2 = new JPasswordField();
            password2.addActionListener(this);
            password2.setBounds(width/2, 540, 200, 50);
            add(password2);
            
            /*
            Explanation of x-coord = (width-300)/4
            The screen is: |____Button_____Button_____Button_____|
            There are three buttons, each of width 100.
            There are four gaps. 3*Button+4*Gap = Width --> Gap = (Width-3*Button)/4
            Each gap has length (width-3*100)/4
            */
            JButton back = new JButton("BACK");
            back.addActionListener(this);
            back.setBounds((width-300)/4, height-150, 100, 50);
            add(back);
            
            JButton save = new JButton("SAVE");
            save.addActionListener(this);
            save.setBounds(100+2*(width-300)/4, height-150, 100, 50);
            add(save);
            
            JButton clear = new JButton("CLEAR");
            clear.addActionListener(this);
            clear.setBounds(200+3*(width-300)/4, height-150, 100, 50);
            add(clear);
        }
        
        // paintComponent method for CrtAcctPanel class
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            // for loop creates white to black gradient for background
            for (int i = 0; i < height; i++) {
                g.setColor(new Color(75, (int)(color - (i*255/height)), 150));
                g.drawLine(0, i, width, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Create Your Account";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("Serif", Font.BOLD, 40);
            g.setColor(Color.RED);
            g.setFont(font);
            strWidth = g.getFontMetrics().stringWidth("Starred (*) fields are required");
            g.drawString("Starred (*) fields are required", (width-strWidth)/2, 175);
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            g.setColor(Color.BLACK);
            strWidth = g.getFontMetrics().stringWidth("Name: ");
            g.drawString("Name: ", width/2-strWidth, 275);
            qMarkPos[0][0] = width/2-strWidth-20-5; qMarkPos[0][1] = 275-20;
            strWidth = g.getFontMetrics().stringWidth("Email: ");
            g.drawString("Email: ", width/2-strWidth, 350);
            qMarkPos[1][0] = width/2-strWidth-20-5; qMarkPos[1][1] = 350-20;
            strWidth = g.getFontMetrics().stringWidth("Username*: ");
            g.drawString("Username*: ", width/2-strWidth, 425);
            qMarkPos[2][0] = width/2-strWidth-20-5; qMarkPos[2][1] = 425-20;
            strWidth = g.getFontMetrics().stringWidth("Password*: ");
            g.drawString("Password*: ", width/2-strWidth, 500);
            qMarkPos[3][0] = width/2-strWidth-20-5; qMarkPos[3][1] = 500-20;
            strWidth = g.getFontMetrics().stringWidth("Re-enter Password*: ");
            g.drawString("Re-enter Password*: ", width/2-strWidth, 575);
            qMarkPos[4][0] = width/2-strWidth-20-5; qMarkPos[4][1] = 575-20;
            for (int i = 0; i < 5; i++) {
                g.drawImage(qMark, qMarkPos[i][0], qMarkPos[i][1], qMarkDiam, qMarkDiam, this);
            }
        }
        
        /**
         * Checks if the given password is valid
         * @param str       Given password
         * @return          True: Password is valid ... False: password is invalid
         */
        public boolean goodPass(String str)
        {
            // Password length: Min = 8 ... Max = 16
            if (str.length() < 8 || str.length() > 16) return false;
            // Valid characters: ASCII 33-126
            for (int i = 0; i < str.length(); i++)
                if (!validChars.contains(str.charAt(i)))
                    return false;
            return true;
        }
        
        /**
         * Append the given information to the text file "User Information.txt" 
         * @param user      Given username
         * @param pass      Given password
         * @param name      Given name (may be empty)
         * @param email     Given email (may be empty)
         */
        public void appendInfo(String user, String pass, String name, String email)
        {
            File file = new File(mainFolderName+File.separator+"User Information.txt");
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
                pw.println(user.trim()+" --- "+encryptRSA(pass.trim())+" --- "+name.trim()+" --- "+email.trim());
            } catch (Exception e) {
                System.err.println("Error with File Writing");
                System.exit(8);
            }
            pw.close();
            readInfoFile();
            repaint();
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String nameStr = name.getText();
            String emailStr = email.getText();
            String userStr = username.getText();
            // getPassword() returns char[] --> use new String() to convert to String
            String passStr = new String(password.getPassword());
            String passStr2 = new String(password2.getPassword());
            String command = e.getActionCommand();
            if (command.equals("BACK")) {
                name.setText(""); nameStr = "";
                email.setText(""); emailStr = "";
                username.setText(""); userStr = "";
                password.setText(""); passStr = "";
                password2.setText(""); passStr2 = "";
                card.show(ctr, "Welcome Panel");
            } else if (command.equals("SAVE")) {
                if (userStr.length() == 0)
                    JOptionPane.showMessageDialog(frame, "Please enter a valid username!");
                else if (!goodPass(passStr)) 
                    JOptionPane.showMessageDialog(frame, "Please enter a valid password!");
                else if (allUsernames.indexOf(userStr) != -1)
                    JOptionPane.showMessageDialog(frame, "Username is taken!");
                else if (!passStr.equals(passStr2))
                    JOptionPane.showMessageDialog(frame, "Passwords do not match!");
                else {
                    appendInfo(userStr, passStr, nameStr, emailStr);
                    JOptionPane.showMessageDialog(frame, "Information saved!");
                    // Create folder for user
                    String folderName = userStr + " - Info";
                    // Create necessary files for user (categories, list of expenses, budget)
                    String fileName1 = "categories - " + userStr + ".csv";
                    String fileName2 = "expenseList - " + userStr + ".csv";
                    String fileName3 = "summary - " + userStr + ".csv";
                    String fileName4 = "vendors - " + userStr + ".txt";
                    File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName1);
                    file.getParentFile().mkdir();
                    try {
                        file.createNewFile();
                        file = new File(mainFolderName+File.separator+folderName+File.separator+fileName2);
                        file.createNewFile();
                        file = new File(mainFolderName+File.separator+folderName+File.separator+fileName3);
                        file.createNewFile();
                        file = new File(mainFolderName+File.separator+folderName+File.separator+fileName4);
                        file.createNewFile();
                    } catch (Exception f) {
                        System.err.println(f);
                    }
                }
                repaint();
            } else { // user clicked "CLEAR"
                name.setText(""); nameStr = "";
                email.setText(""); emailStr = "";
                username.setText(""); userStr = "";
                password.setText(""); passStr = "";
                password2.setText(""); passStr2 = "";
                repaint();
            }
        }
        
        public void mouseClicked(MouseEvent e)
        {
            int clickX = e.getX();
            int clickY = e.getY();
            /*
            The top left coordinates for the username help button are (x, y).
            Its center is (x+qMarkDiam/2, y+qMarkDiam/2) and its
            radius = qMarkDiam/2. The equation for this can be written as
            (clickX-x-qMarkDiam/2)^2 + (clicKY-y-qMarkDiam/2)^2 = qMarkDiam^2. 
            If we substitute the values  clickX and clickY into the equation and 
            the result is <= qMarkDiam^2, the click occurred within the help button 
            and we must show the JOptionPane.
            */
            for (int i = 0; i < 5; i++) {
                int x = qMarkPos[i][0];
                int y = qMarkPos[i][1];
                if (Math.pow(clickX-x-qMarkDiam/2, 2) + Math.pow(clickY-y-qMarkDiam/2, 2) <= Math.pow(qMarkDiam, 2)) {
                    String str = "";
                    if (i == 0)
                        str = "Enter your name here. This field is OPTIONAL.";
                    else if (i == 1)
                        str = "Enter your email here. This field is OPTIONAL.";
                    else if (i == 2)
                        str = "Enter your username here. This field is REQUIRED.";
                    else if (i == 3)
                        str = "Enter your password here. Password length must be between 8 and 16 characters, inclusive. This field is REQUIRED.";
                    else
                        str = "Re-enter your password here. This field is REQUIRED.";
                    showHelpMessage(str);
                }
            }
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    }
    
    /**
     * Screen that shows as soon as user enters the app (after logging in)
     */
    class ProfilePanel extends JPanel implements ActionListener, MouseListener
    {
        int qMarkX, qMarkY, qMarkDiam;
        //JComboBox optionsBtn;
        
        public ProfilePanel()
        {
            setLayout(null);
            addMouseListener(this);
            qMarkX = 10; qMarkY = 10; qMarkDiam = 40;
            
            /*JButton editButton = new JButton("Edit Profile");
            editButton.addActionListener(this);
            editButton.setBounds(100, 50, 200, 50);
            add(editButton);*/
            
            JButton entExpBtn = new JButton("Enter Expense");
            //entExpBtn.setFont(new Font("Arial", Font.BOLD, 17));
            entExpBtn.addActionListener(this);
            entExpBtn.setBounds(100, 300, 200, 50);
            add(entExpBtn);
            
            JButton addCatBtn = new JButton("Add/Rem Categories");
            //addCatBtn.setFont(new Font("Arial", Font.BOLD, 14));
            addCatBtn.addActionListener(this);
            addCatBtn.setBounds(100, 400, 200, 50);
            add(addCatBtn);
            
            JButton addRemBtn = new JButton("Add/Rem Vendors");
            addRemBtn.addActionListener(this);
            addRemBtn.setBounds(100, 500, 200, 50);
            add(addRemBtn);
            
            JButton budgBtn = new JButton("Edit Categories");
            budgBtn.addActionListener(this);
            budgBtn.setBounds(width-300, 300, 200, 50);
            add(budgBtn);
            
            JButton expRepBtn = new JButton("Expense Report");
            expRepBtn.addActionListener(this);
            expRepBtn.setBounds(width-300, 400, 200, 50);
            add(expRepBtn);
            
            JButton sumBtn = new JButton("View Summary");
            sumBtn.addActionListener(this);
            sumBtn.setBounds(width-300, 500, 200, 50);
            add(sumBtn);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(this);
            logoutBtn.setBounds(width-300, 50, 200, 50);
            add(logoutBtn);
            
            // items added to optionsBtn in WelcomePanel immediately after successfully logging in
            // items added --> username, "Change Password", "Help", "Logout"
            /*optionsBtn = new JComboBox();
            optionsBtn.addActionListener(this);
            optionsBtn.setBounds(width-230, 120, 160, 30);
            add(optionsBtn);*/
            
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            if (command.equals("Edit Profile")) {
                // button removed but keep this statement in case we choose to use the button
            } else if (command.equals("Enter Expense")) {
                userCategories.clear();
                fillCategoriesList("categories - "+currentUsername+".csv");
                for(int i = 0; i < 4; i++) {
                    ep.catBoxes[i].removeAllItems();
                    ep.catBoxes[i].addItem("Select a Category");
                    for (int j = 0; j < userCategories.size(); j++)
                        ep.catBoxes[i].addItem(userCategories.get(j).getName());
                }
                userVendors.clear();
                fillVendorsList("vendors - "+currentUsername+".txt");
                for (int index = 0; index < 4; index++) {
                    ep.inUseVendors[index].removeAllItems();
                    ep.inUseVendors[index].addItem("Select a Vendor");
                    for (int j = 0; j < userVendors.size(); j++)
                        ep.inUseVendors[index].addItem(userVendors.get(j));
                }
                card.show(ctr, "Expense Panel");
            } else if (command.equals("Add/Rem Categories")) {
                userCategories.clear();
                fillCategoriesList("categories - "+currentUsername+".csv");
                for(int i = 0; i < 5; i++) {
                    acp.remCatBoxes[i].removeAllItems();
                    acp.remCatBoxes[i].addItem("Select a Category");
                    for (int j = 0; j < userCategories.size(); j++)
                        acp.remCatBoxes[i].addItem(userCategories.get(j).getName());
                }
                card.show(ctr, "AddRem Category Panel");
            } else if (command.equals("Add/Rem Vendors")) {
                userVendors.clear();
                fillVendorsList("vendors - "+currentUsername+".txt");
                for (int i = 0; i < 5; i++) {
                    vp.remVendorBoxes[i].addItem("Select a Vendor");
                    for (int j = 0; j < userVendors.size(); j++)
                        vp.remVendorBoxes[i].addItem(userVendors.get(j));
                }
                card.show(ctr, "Vendor Panel");
            } else if (command.equals("Expense Report")) {
                entriesList.clear();
                fillEntriesList("expenseList - "+currentUsername+".csv");
                userVendors.clear();
                fillVendorsList("vendors - "+currentUsername+".txt");
                userCategories.clear();
                fillCategoriesList("categories - "+currentUsername+".csv");
                // add items to combo boxes (drop down) in ExpRepPanel class
                erp.catBox.removeAllItems();
                erp.catBox.addItem("ALL");
                for (int i = 0; i < userCategories.size(); i++) {
                    erp.catBox.addItem(userCategories.get(i).getName().toLowerCase());
                }
                erp.vendorBox.removeAllItems();
                erp.vendorBox.addItem("ALL");
                for (int i = 0; i < userVendors.size(); i++) {
                    erp.vendorBox.addItem(userVendors.get(i).toLowerCase());
                }
                card.show(ctr, "ExpRep Panel");
            } else if (command.equals("View Summary")) {
                userCategories.clear();
                fillCategoriesList("categories - "+currentUsername+".csv");
                entriesList.clear();
                fillEntriesList("expenseList - "+currentUsername+".csv");
                vsp.setDefaultPanelDim();
                vsp.setYearBox();
                vsp.setTextFields();
                card.show(ctr, "View Summary Panel");
            } else if (command.equals("Edit Categories")) {
                userCategories.clear();
                fillCategoriesList("categories - "+currentUsername+".csv");
                // refill data structure in EditCategoryPanel (sbp)
                for(int i = 0; i < 10; i++) {
                    sbp.catNames[i].removeAllItems();
                    sbp.catNames[i].addItem("Select a Category");
                    for (int j = 0; j < userCategories.size(); j++)
                        sbp.catNames[i].addItem(userCategories.get(j).getName());
                }
                card.show(ctr, "Edit Category Panel");
            } else if (command.equals("Logout")) {
                // button removed as of now
                card.show(ctr, "Welcome Panel");
            }
            
            /*String optionChosen = (String)(optionsBtn.getSelectedItem());
            if (optionChosen.equals(currentUsername)) {
                // do nothing, this is the default option
            } else if (optionChosen.equals("Change Password")) {
                // go to change password screen
                optionsBtn.setSelectedItem(currentUsername);
            } else if (optionChosen.equals("Help")) {
                optionsBtn.setSelectedItem(currentUsername);
                // show help message
                showProfilePanelHelp();
            } else if (optionChosen.equals("Logout")) {
                optionsBtn.setSelectedItem(currentUsername);
                card.show(ctr, "Welcome Panel");
            }*/
        }
        
        /** 
         * Shows help message specifically for the Profile Panel
         */
        public void showProfilePanelHelp()
        {
            String str = "Edit Profile: Edit name, email, change password, etc.\n"; // describe each button
            str += "Enter Expense: Enter an expense/payment made.\n";
            str += "Add/Rem Categories: Add or remove an expense category in the system.\n";
            str += "Add/Rem Vendors: Add or remove a vendor in the system.\n";
            str += "Expense Report: View an expense report for user-specified date range.\n";
            str += "Logout: Log out of your account and return to main screen.\n";
            str += "Edit Categories: Change symbols and budgets for defined categories.\n";
            str += "View Summary: View a summary of all expenses across a year.\n";
            //showHelpMessage(str);
            JOptionPane.showMessageDialog(frame, str);
        }
        
        /**
         *
            The top left coordinates for the username help button are (qMarkX, qMarkY).
            Its center is (qMarkX+qMarkDiam/2, qMarkY+qMarkDiam/2) and its
            radius = qMarkDiam/2. The equation for this can be written as
            (clickX-qMarkX-qMarkDiam/2)^2 + (clicKY-qMarkY-qMarkDiam/2)^2 = qMarkDiam^2. 
            If we substitute the values  clickX and clickY into the equation and 
            the result is <= qMarkDiam^2, the click occurred within the help button 
            and we must show the JOptionPane.
         */
        public void mouseClicked(MouseEvent e)
        {
            int clickX = e.getX();
            int clickY = e.getY();
            if (Math.pow(clickX-qMarkX-qMarkDiam/2, 2) + Math.pow(clickY-qMarkY-qMarkDiam/2, 2) <= Math.pow(qMarkDiam, 2)) {
                showProfilePanelHelp();
            }
        }
        
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {} 
        
        // paintComponent method for ProfilePanel class
        public void paintComponent (Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            for (int i = 0; i < height; i++) {
                g.setColor(new Color(123, (int)(color - (i*255/height)), 123));
                g.drawLine(0, i, width, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            Image profilePic = new ImageIcon(mainFolderName+File.separator+"profilepic.png").getImage();
            g.drawImage(profilePic, width/2-200, 190, 400, 400, this);
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Profile Page";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("Arial", Font.BOLD, 40);
            g.setFont(font);
            strWidth = g.getFontMetrics().stringWidth(currentUsername);
            g.drawString(currentUsername, (width-strWidth)/2, height/2+200+20);
            g.drawImage(qMark, qMarkX, qMarkY, qMarkDiam, qMarkDiam, this);
        }
    }
    
    class ExpensePanel extends JPanel implements ActionListener
    {
        JComboBox[] days, months;
        JTextField[] yearEntry, vendEntry, descEntry, amtEntry;
        JComboBox[] catBoxes, inUseVendors;
        JCheckBox[] saveVendorBoxes;
        boolean[] saveVendors = new boolean[4];
        String[] monthsGiven = new String[4];
        // Note that day = "0" if the index selected is 0 --> account for this
        String[] daysGiven = new String[4];
        String[] yearsGiven = new String[4];
        String[] vendGiven = new String[4]; // vendor given by text field
        String[] vendGiven2 = new String[4]; // vendor given by combo box
        String[] descGiven = new String[4];
        String[] catGiven = new String[4];
        String[] amtGiven = new String[4];
        String prevButton = ""; // previous button clicked
        String[] curDateValues = new String[3];
        
        public ExpensePanel()
        {
            setLayout(null);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(this);
            logoutBtn.setBounds(width-300, 50, 200, 50);
            add(logoutBtn);
            
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(this);
            clearBtn.setBounds(width-300, 300, 200, 50);
            add(clearBtn);
            
            JButton saveBtn = new JButton("Save"); 
            saveBtn.addActionListener(this);
            saveBtn.setBounds(width-300, 400, 200, 50);
            add(saveBtn);
            
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(this);
            backBtn.setBounds(width-300, 500, 200, 50);
            add(backBtn);
            
            months = new JComboBox[4];
            days = new JComboBox[4];
            yearEntry = new JTextField[4];
            vendEntry = new JTextField[4];
            descEntry = new JTextField[4];
            catBoxes = new JComboBox[4];
            amtEntry = new JTextField[4];
            saveVendorBoxes = new JCheckBox[4];
            inUseVendors = new JComboBox[4];
            
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    int index = row*2 + col;
                    months[index] = new JComboBox(monthsList);
                    months[index].addActionListener(this);
                    months[index].setBounds(205+500*row, 165+300*col, 100, 30);
                    add(months[index]);

                    days[index] = new JComboBox();
                    days[index].addItem("Day");
                    for (int i = 1; i <= 31; i++) {
                        days[index].addItem(""+i);
                    }
                    days[index].addActionListener(this);
                    days[index].setBounds(310+500*row, 165+300*col, 50, 30);
                    add(days[index]);

                    yearEntry[index] = new JTextField();
                    yearEntry[index].addActionListener(this);
                    yearEntry[index].setBounds(365+500*row, 165+300*col, 100, 30);
                    add(yearEntry[index]);
                    
                    vendEntry[index] = new JTextField();
                    vendEntry[index].addActionListener(this);
                    vendEntry[index].setBounds(205+500*row, 205+300*col, 150, 30);
                    vendEntry[index].setHorizontalAlignment(JTextField.CENTER); // enter text from the center
                    add(vendEntry[index]);
                    
                    inUseVendors[index] = new JComboBox();
                    inUseVendors[index].addItem("Select a Vendor");
                    for (int j = 0; j < userVendors.size(); j++)
                        inUseVendors[index].addItem(userVendors.get(j));
                    inUseVendors[index].addActionListener(this);
                    inUseVendors[index].setBounds(360+500*row, 205+300*col, 150, 30);
                    add(inUseVendors[index]);
                    
                    descEntry[index] = new JTextField();
                    descEntry[index].addActionListener(this);
                    descEntry[index].setBounds(205+500*row, 245+300*col, 200, 30);
                    descEntry[index].setHorizontalAlignment(JTextField.CENTER); // enter text from the center
                    add(descEntry[index]);
                    
                    catBoxes[index] = new JComboBox();
                    catBoxes[index].addItem("Select a Category");
                    for (int j = 0; j < userCategories.size(); j++)
                        catBoxes[index].addItem(userCategories.get(j).getName());
                    catBoxes[index].addActionListener(this);
                    catBoxes[index].setBounds(205+500*row, 285+300*col, 150, 30);
                    add(catBoxes[index]);

                    amtEntry[index] = new JTextField();
                    amtEntry[index].addActionListener(this);
                    amtEntry[index].setBounds(205+500*row, 325+300*col, 100, 30);
                    amtEntry[index].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // enter text from the right
                    add(amtEntry[index]);
                    
                    saveVendorBoxes[index] = new JCheckBox("Save Vendor");
                    saveVendorBoxes[index].addActionListener(this);
                    saveVendorBoxes[index].setBounds(205+500*row, 360+300*col, 100, 30);
                    add(saveVendorBoxes[index]);
                }
            }
            curDateValues = setInitialDateValues();
            
        }
        
        /**
         * Sets the initial month/day/year to today's date
         * @return          Current month, day, year
         */
        public String[] setInitialDateValues()
        {
            LocalDate today = LocalDate.now();
            int curDay = today.getDayOfMonth();
            int curYear = today.getYear();
            String curMonth = today.getMonth().toString();
            String curMonthFormatted = curMonth.charAt(0) + curMonth.substring(1).toLowerCase();
            for (int i = 0; i < 4; i++) {
                months[i].setSelectedItem(curMonthFormatted);
                days[i].setSelectedItem(curDay+"");
                yearEntry[i].setText(curYear+"");
            }
            return new String[] {curMonthFormatted, curDay+"", curYear+""};
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            // For reference: Top Left = 0, Bottom Left = 1, Top Right = 2, Bottom Right = 3
            for (int i = 0; i < 4; i++) {
                monthsGiven[i] = monthsList[months[i].getSelectedIndex()];
                daysGiven[i] = ""+days[i].getSelectedIndex();
                yearsGiven[i] = yearEntry[i].getText();
                vendGiven[i] = vendEntry[i].getText();
                vendGiven2[i] = (String)(inUseVendors[i].getSelectedItem());
                descGiven[i] = descEntry[i].getText();
                catGiven[i] = (String)(catBoxes[i].getSelectedItem());
                amtGiven[i] = amtEntry[i].getText();
                saveVendors[i] = saveVendorBoxes[i].isSelected();
            }
            if (command.equals("Logout")) {
                clearExpenseInfo(new boolean[] {true,true,true,true});
                card.show(ctr, "Welcome Panel");
            } else if (command.equals("Clear")) {
                clearExpenseInfo(new boolean[] {true,true,true,true});
            } else if (command.equals("Save")) {
                boolean[] indicesToClear = saveExpenseChanges();
                clearExpenseInfo(indicesToClear);
            } else if (command.equals("Back")) {
                clearExpenseInfo(new boolean[] {true,true,true,true});
                card.show(ctr, "Profile Panel");
            } 
            prevButton = command;
        }
        
        /**
         * Saves any changes made to expenses
         * @return      Boolean array of indices saved
         */
        public boolean[] saveExpenseChanges()
        {
            boolean[] saved = {false, false, false, false};
            String popupMsg = "";
            for (int i = 0; i < 4; i++) {
                popupMsg += ("ADD Expense "+(i+1)+": ");
                String catMsg = "";
                boolean useFieldVendor = true; // true if JTextField is used for vendor entry
                if (vendGiven2[i].equals("Select a Vendor")) {// if no vendor selected in combo box
                    useFieldVendor = true;
                    catMsg = getExpMsg(monthsGiven[i], daysGiven[i], yearsGiven[i],
                        vendGiven[i], descGiven[i], catGiven[i], amtGiven[i]);
                } else if (vendGiven[i].equals("")) { // if no vendor entered in JTextField
                    useFieldVendor = false;
                    catMsg = getExpMsg(monthsGiven[i], daysGiven[i], yearsGiven[i],
                        vendGiven2[i], descGiven[i], catGiven[i], amtGiven[i]);
                } else { // if a value is entered in JTextField and JComboBox
                    useFieldVendor = true;
                    if (vendGiven[i].equalsIgnoreCase(vendGiven2[i])) { // vendor entered in both are same
                        catMsg = getExpMsg(monthsGiven[i], daysGiven[i], yearsGiven[i],
                        vendGiven2[i], descGiven[i], catGiven[i], amtGiven[i]);
                    } else { // vendor entered in both are different
                        catMsg = "Bad vendor";
                    }
                }
                if (catMsg.equals("Default")) {
                    popupMsg += "Empty";
                } else if (catMsg.equals("Bad date")) {
                    popupMsg += ("Invalid date.");
                } else if (catMsg.equals("Cat not found")) {
                    popupMsg += ("Invalid category.");
                } else if (catMsg.equals("Bad amount")) {
                    popupMsg += ("Invalid amount.");
                } else if (catMsg.equals("Zero amount")) {
                    popupMsg += ("Please enter a nonzero amount.");
                } else if (catMsg.equals("Commas used")) {
                    popupMsg += ("Please avoid using commas.");
                } else if (catMsg.equals("Bad vendor")) {
                    popupMsg += ("Invalid vendor.");
                } else if (catMsg.equals("Good expense")) {
                    popupMsg += ("Added!");
                    vendGiven[i] = vendGiven[i].toLowerCase();
                    String vendorToUse = "";
                    if (useFieldVendor)
                        vendorToUse = vendGiven[i];
                    else
                        vendorToUse = vendGiven2[i];
                    appendExp(monthsGiven[i], daysGiven[i], yearsGiven[i],
                        vendorToUse, descGiven[i], catGiven[i], amtGiven[i]);
                    if (saveVendors[i]) {
                        appendVendor(vendorToUse);
                        userVendors.clear();
                        fillVendorsList("vendors - "+currentUsername+".txt");
                        for (int index = 0; index < 4; index++) {
                            inUseVendors[index].addItem("Select a Vendor");
                            for (int j = 0; j < userVendors.size(); j++)
                                inUseVendors[index].addItem(userVendors.get(j));
                        }
                    }
                        
                    saved[i] = true;
                } 
                popupMsg += "\n";
            }
            JOptionPane.showMessageDialog(frame, popupMsg);
            return saved;
        }
        
        /**
         * Appends expense information to expense csv file
         * @param month         Month given
         * @param day           Day given
         * @param year          Year given
         * @param vendor        Vendor given
         * @param desc          Description given
         * @param cat           Category given
         * @param amt           Amount given
         */
        public void appendExp(String month, String day, String year, String vendor, String desc, String cat, String amt)
        {
            int monthInd = 0;
            for (int i = 0; i < monthsList.length; i++) {
                if (monthsList[i].equals(month)) {
                    monthInd = i;
                    break;
                }
            }
            String folderName = currentUsername + " - Info";
            String fileName = "expenseList - " + currentUsername + ".csv";
            File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
                String date = monthInd+"/"+day+"/"+year;
                Category searchCatName = new Category(cat.toLowerCase(), "", 0);
                Category searchCatSymb = new Category("", cat.toUpperCase(), 0);
                int nameIndex = userCategories.indexOf(searchCatName);
                int symbIndex = userCategories.indexOf(searchCatSymb);
                String symbol = "";
                if (nameIndex != -1)
                    symbol = userCategories.get(nameIndex).getSymbol().toUpperCase();
                else if (symbIndex != -1) 
                    symbol = userCategories.get(symbIndex).getSymbol().toUpperCase();
                if (amt.charAt(0) == '$')
                    amt = amt.substring(1);
                pw.println(date+","+vendor.toLowerCase()+","+desc.toLowerCase()+","+symbol+","+amt);
            } catch (Exception e) {
                System.err.println("Error with File Writing");
                System.exit(9);
            }
            pw.close();
        }
        
        /**
         * Returns a message associated with the given expense information
         * @param month         Month given
         * @param day           Day given
         * @param year          Year given
         * @param vendor        Vendor given
         * @param desc          Description given
         * @param cat           Category given
         * @param amt           Amount given
         * @return              String: associated message
         */
        public String getExpMsg(String month, String day, String year, String vendor, String desc, String cat, String amt)
        {
            // all values are default
            if (month.equals(curDateValues[0]) && day.equals(curDateValues[1]) && year.equals(curDateValues[2])
                    && vendor.equals("") && desc.equals("") && cat.equals("Select a Category") && amt.equals(""))
                return "Default";
            // other values are changed but some aspect of date is default/incorrect
            // day.equals("0") --> the item selected was "Day"
            if(month.equals("Month") || day.equals("Day") || year.equals("Year")
                    || day.equals("0") || !isValidDate(month, day, year)) 
                return "Bad date";
            if (vendor.equals(""))
                return "Bad vendor";
            // No commas should be used
            // category not checked because this happens later
            if (vendor.contains(",") || desc.contains(",") || amt.contains(","))
                return "Commas used";
            /*Category searchCatName = new Category(cat.toLowerCase(), "", 0);
            Category searchCatSymb = new Category("", cat.toUpperCase(), 0);
            if (userCategories.indexOf(searchCatName) == -1 &&
                    userCategories.indexOf(searchCatSymb) == -1)
                return "Cat not found";*/
            if (cat.equals("Select a Category"))
                return "Cat not found";
            if (amt.length() == 0)
                return "Bad amount";
            if (amt.charAt(0) == '$')
                amt = amt.substring(1);
            double amount = 0;
            try {
                amount = Double.parseDouble(amt);
            } catch (Exception e) {
                return "Bad amount";
            }
            if (amount == 0)
                return "Zero amount";
            return "Good expense";
        }
        
        /**
         * Checks if the given information forms a valid date
         * @param m         Month
         * @param d         Day
         * @param y         Year
         * @return          True if valid ... False if invalid
         */
        public boolean isValidDate(String m, String d, String y)
        {
            /*
            Jan, Mar, May, Jul, Aug, Oct, Dec have 31 days
                These months have indices 1, 3, 5, 7, 8, 10, 12
            Apr, Jun, Sep, Nov have 30 days
                These months have indices 4, 6, 9, 11
            Feb has 28 days --> 29 days if leap year
                This month has index 2
            */
            int month = 0;
            for (int i = 0; i < monthsList.length; i++) {
                if (monthsList[i].equals(m)) {
                    month = i;
                    break;
                }
            }
            int day = Integer.parseInt(d);
            int year = 0;
            try {
                year = Integer.parseInt(y);
            } catch (Exception e) {
                return false;
            }
            LocalDate today = LocalDate.now();
            int curDay = today.getDayOfMonth();
            int curYear = today.getYear();
            String curMonth = today.getMonth().toString();
            int curMonthIndex = 0;
            for (int i = 0; i < monthsList.length; i++) {
                if (monthsList[i].equalsIgnoreCase(curMonth)) {
                    curMonthIndex = i;
                    break;
                }
            }
            // Given date is invalid if it is after today's date
            if (givenIsAfterToday(month, day, year, curMonthIndex, curDay, curYear))
                return false;
            if ((month == 1 || month == 3 || month == 5 || month == 7 || 
                    month == 8 || month == 10 || month == 12) && day <= 31)
                return true;
            if ((month == 4 || month == 6 || month == 9 || month == 11) && day <= 30)
                return true;
            if (month == 2) {
                boolean isLeap = isLeapYear(year);
                if (isLeap && day <= 29)
                    return true;
                if (!isLeap && day <= 28)
                    return true;
            }
            return false;
        }
        
        /**
         * Clears all the information on the ExpensePanel screen
         * @param arr       Array of boolean values --> if true, then clear
         */
        public void clearExpenseInfo(boolean[] arr)
        {
            for (int i = 0; i < 4; i++) {
                if (arr[i]) {
                    setInitialDateValues();
                    vendEntry[i].setText("");  
                    descEntry[i].setText(""); 
                    catBoxes[i].setSelectedItem("Select a Category");
                    inUseVendors[i].setSelectedItem("Select a Vendor");
                    amtEntry[i].setText("");
                    monthsGiven[i] = "Month"; daysGiven[i] = "Day";
                    yearsGiven[i] = "Year"; vendGiven[i] = "";  vendGiven2[i] = "";
                    descGiven[i] = ""; catGiven[i] = ""; amtGiven[i] = "";
                    saveVendorBoxes[i].setSelected(false);
                    saveVendors[i] = false;
                }
            }
        }
        
        // paintComponent method for ExpensePanel class
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            for (int i = 0; i < height; i++) {
                g.setColor(new Color(23, (int)(color - (i*255/height)), 23));
                g.drawLine(0, i, width, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Enter Expenses";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 2; col++) {
                    strWidth = g.getFontMetrics().stringWidth("Date: ");
                    g.drawString("Date: ", (200-strWidth)+500*row, 190+300*col);
                    strWidth = g.getFontMetrics().stringWidth("Vendor: ");
                    g.drawString("Vendor: ", (200-strWidth)+500*row, 230+300*col);
                    strWidth = g.getFontMetrics().stringWidth("Description: ");
                    g.drawString("Description: ", (200-strWidth)+500*row, 270+300*col);
                    strWidth = g.getFontMetrics().stringWidth("Category: ");
                    g.drawString("Category: ", (200-strWidth)+500*row, 310+300*col);
                    g.setColor(Color.WHITE);
                    g.fillOval(200-strWidth+500*row, 190+300*col-50, 40, 40);
                    g.setColor(Color.BLACK);
                    g.drawString((2*row+col+1)+"", 200-strWidth+500*row+13, 190+300*col-50+30);
                    strWidth = g.getFontMetrics().stringWidth("Amount: ");
                    g.drawString("Amount: ", (200-strWidth)+500*row, 350+300*col);
                }
            }
        }
    }
    
    class AddRemCategoryPanel extends JPanel implements ActionListener
    {
        JTextField[] catNames, catSymbs, catBudgets;
        String[] namesGiven, symbsGiven, budgetsGiven;
        String[] remCatGiven;
        JComboBox[] remCatBoxes;
        String prevButtonPressed = "";
        
        public AddRemCategoryPanel()
        {
            setLayout(null);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(this);
            logoutBtn.setBounds(width-300, 50, 200, 50);
            add(logoutBtn);
            
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(this);
            clearBtn.setBounds(width-300, 200, 200, 50);
            add(clearBtn);
            
            JButton saveBtn = new JButton("Save"); 
            saveBtn.addActionListener(this);
            saveBtn.setBounds(width-300, 300, 200, 50);
            add(saveBtn);
            
            JButton addBtn = new JButton("Add More");
            addBtn.addActionListener(this);
            addBtn.setBounds(width-300, 400, 200, 50);
            add(addBtn);
            
            JButton remBtn = new JButton("Remove More");
            remBtn.addActionListener(this);
            remBtn.setBounds(width-300, 500, 200, 50);
            add(remBtn);
            
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(this);
            backBtn.setBounds(width-300, 600, 200, 50);
            add(backBtn);
            
            catNames = new JTextField[5];
            catSymbs = new JTextField[5];
            catBudgets = new JTextField[5];
            remCatBoxes = new JComboBox[5];
                        
            for (int i = 0; i < 5; i++) {
                catNames[i] = new JTextField("");
                catNames[i].addActionListener(this);
                catNames[i].setBounds(205, 200+100*i, 150, 50);
                catNames[i].setHorizontalAlignment(JTextField.CENTER); // enter text from the center
                add(catNames[i]);
                
                catSymbs[i] = new JTextField("");
                catSymbs[i].addActionListener(this);
                catSymbs[i].setBounds(360, 200+100*i, 75, 50);
                catSymbs[i].setHorizontalAlignment(JTextField.CENTER); // enter text from the center
                add(catSymbs[i]);
                
                catBudgets[i] = new JTextField("");
                catBudgets[i].addActionListener(this);
                catBudgets[i].setBounds(440, 200+100*i, 100, 50);
                catBudgets[i].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // enter text from the right
                add(catBudgets[i]);
                
                remCatBoxes[i] = new JComboBox();
                remCatBoxes[i].addItem("Select a Category");
                for (int j = 0; j < userCategories.size(); j++)
                    remCatBoxes[i].addItem(userCategories.get(j).getName());
                remCatBoxes[i].addActionListener(this);
                remCatBoxes[i].setBounds(800, 200+100*i, 145, 50); 
                add(remCatBoxes[i]);
            }
            
            namesGiven = new String[5];
            symbsGiven = new String[5];
            budgetsGiven = new String[5];
            remCatGiven = new String[5];
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            for (int i = 0; i < 5; i++) {
                namesGiven[i] = catNames[i].getText();
                symbsGiven[i] = catSymbs[i].getText();
                budgetsGiven[i] = catBudgets[i].getText();
                //remCatGiven[i] = remCatSpaces[i].getText();
                remCatGiven[i] = (String)(remCatBoxes[i].getSelectedItem());
            }
            boolean[] indicesToSave = new boolean[10];
            if (command.equals("Logout")) {
                clearCategoryInfo(indicesToSave);
                card.show(ctr, "Welcome Panel");
            } else if (command.equals("Clear")) {
                clearCategoryInfo(indicesToSave);
            } else if (command.equals("Save")) {
                indicesToSave = saveCategoryChanges();
                clearCategoryInfo(indicesToSave);
            } else if (command.equals("Add More")) {
                // if previous button was not save, then save is needed before clear
                if (!prevButtonPressed.equals("Save"))
                    saveCategoryChanges(); 
                // save the JTextFields associated with removing categories
                for (int i = 5; i < 10; i++)
                    indicesToSave[i] = true;
                clearCategoryInfo(indicesToSave);
            } else if (command.equals("Remove More")) {
                // if previous button was not save, then save is needed before clear
                if (!prevButtonPressed.equals("Save"))
                    saveCategoryChanges();
                // save the JTextFields associated with adding categories
                for (int i = 0; i < 5; i++)
                    indicesToSave[i] = true;
                clearCategoryInfo(indicesToSave);
            } else if (command.equals("Back")) {
                clearCategoryInfo(indicesToSave);
                card.show(ctr, "Profile Panel");
            }
            
            prevButtonPressed = command;
        }
        
        /**
         * Saves any additions or removals of categories
         */
        public boolean[] saveCategoryChanges()
        {
            boolean[] saved = new boolean[10];
            for (int i = 0; i < 10; i++)
                saved[i] = true;
            // Add categories
            String popupMsg = "";
            for (int i = 0; i < 5; i++) {
                popupMsg += ("ADD Category "+(i+1)+": ");
                String catMsg = getCatMsgAdd(namesGiven[i], symbsGiven[i], budgetsGiven[i]);
                if (catMsg.equals("Default")) {
                    popupMsg += "Empty";
                } else if (catMsg.equals("Bad name")) {
                    popupMsg += ("Invalid category name.");
                } else if (catMsg.equals("Bad symbol")) {
                    popupMsg += ("Invalid symbol.");
                } else if (catMsg.equals("Bad budget")) {
                    popupMsg += ("Invalid budget.");
                } else if (catMsg.equals("Negative budget")) {
                    popupMsg += ("Please enter a nonnegative budget.");
                } else if (catMsg.contains("In use")) {
                    popupMsg += ("This name/symbol is taken.");
                } else if (catMsg.equals("Good category")) {
                    popupMsg += ("Added!");
                    if (budgetsGiven[i].equals("")) {
                        budgetsGiven[i] = "0";
                        
                    }
                    saved[i] = false; // if category is saved, then we can clear it (i.e. don't save on screen)
                    Category newCat = new Category(namesGiven[i], symbsGiven[i], budgetsGiven[i]);
                    appendCat(newCat);
                    userCategories.add(newCat);
                    
                } 
                popupMsg += "\n";
            }
            
            JOptionPane.showMessageDialog(frame, popupMsg);
            // Remove categories
            popupMsg = "";
            for (int i = 0; i < 5; i++) {
                popupMsg += ("REM Category "+(i+1)+": ");
                String catMsg = getCatMsgRem(remCatGiven[i]);
                if (catMsg.contains("Found")) {
                    int index = Integer.parseInt(catMsg.substring(catMsg.indexOf("-")+1).trim());
                    userCategories.remove(index);
                    rewriteCategories("categories - "+currentUsername+".csv");
                    popupMsg += "Removed!";
                    saved[i+5] = true; // add 5 to account for the booleans assigned to the Add categories
                } else if (catMsg.equals("Not found")) {
                    popupMsg += ("Given category not found.");
                } else if (catMsg.equals("Default")) {
                    popupMsg += "Empty";
                }
                popupMsg += "\n";
            }
            for (int i = 0; i < 5; i++) {
                remCatBoxes[i].removeAllItems();
                remCatBoxes[i].addItem("Select a Category");
                for (int j = 0; j < userCategories.size(); j++)
                    remCatBoxes[i].addItem(userCategories.get(j).getName());
            }
            JOptionPane.showMessageDialog(frame, popupMsg);
            
            return saved; 
        }
        
        /**
         * Returns a message associated with the given category information
         * This method checks given category information only for REMOVING categories
         * @param str       Given category name/symbol
         * @return          String: associated message
         */
        public String getCatMsgRem(String str)
        {
            if (str.equals("Select a Category"))
                return "Default";
            if (str.equals(""))
                return "Default";
            str = str.toLowerCase();
            Category cat = new Category(str, "", 0);
            int index = userCategories.indexOf(cat);
            if (index != -1)
                return ("Found - " + index);
            cat.setName("");
            str = str.toUpperCase();
            cat.setSymbol(str);
            index = userCategories.indexOf(cat);
            if (index != -1)
                return ("Found - " + index);
            return "Not found";
        }
        
        /**
         * Append the given information to the categories csv file
         * @param cat       Given category
         */
        public void appendCat(Category cat)
        {
            String folderName = currentUsername + " - Info";
            String fileName = "categories - " + currentUsername + ".csv";
            File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
                pw.println(cat.getName().trim()+","+cat.getSymbol().trim()+","+cat.getBudget());
            } catch (Exception e) {
                System.err.println("Error with File Writing");
                System.exit(9);
            }
            pw.close();
        }
        
        /**
         * Returns a message associated with the given category information
         * This method checks given category information only for ADDING categories
         * @param name          Given category name
         * @param symb          Given category symbol
         * @param budgStr       Given category budget
         * @return              String: Associated message
         */
        public String getCatMsgAdd(String name, String symb, String budgStr)
        {
            // Default values found
            if (name.equals("") && symb.equals("") && budgStr.equals(""))
                return "Default";
            // User made changes
            if (name.equals("") || name.indexOf(',') != -1)
                return "Bad name";
            if (symb.equals("") || symb.length() > 5 || symb.indexOf(',') != -1)
                return "Bad symbol";
            // if no value entered for budget, then set it to default $0
            if (budgStr.equals(""))
                budgStr = "0";
            // remove dollar sign if it is there in budgStr
            if (budgStr.charAt(0) == '$') 
                budgStr = budgStr.substring(1);
            double budget = 0;
            try {
                budget = Double.parseDouble(budgStr);
            } catch (Exception e) {
                return "Bad budget";
            }
            if (budget < 0) return "Negative budget";
            name = name.toLowerCase();
            symb = symb.toUpperCase();
            Category c = new Category(name, symb, budget);
            if (userCategories.indexOf(c) != -1) 
                return "In use";
            return "Good category";
        }
        
        /**
         * Clears all the information on the AddRemCategoryPanel screen
         * @param arr      Array of boolean values dictating which JTextFields to save
         *                 True --> Save ... False --> Don't save on screen (i.e. clear)
         *                 arr.length = 10
         *                     Indices 0-4: Represent JTextFields for adding
         *                     Indices 5-9: Represent JTextFields for removing
         */
        public void clearCategoryInfo(boolean[] arr)
        {
            for (int i = 0; i < arr.length; i++) {
                if (i < 5 && !arr[i]) {
                    catNames[i].setText(""); catSymbs[i].setText("");
                    catBudgets[i].setText(""); 
                    namesGiven[i] = ""; symbsGiven[i] = "";
                    budgetsGiven[i] = "";
                }
                if (i >= 5 && !arr[i]) {
                    // Shift i down by 5 since the indices for remCatBoxes array are 0-4
                    remCatBoxes[i-5].setSelectedItem("Select a Category");
                    remCatGiven[i-5] = "";
                }
                
            }
        }
        
        // paintComponent method for AddRemCategoryPanel class
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            for (int i = 0; i < height; i++) {
                g.setColor(new Color(73, (int)(color - (i*255/height)), 35));
                g.drawLine(0, i, width, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Edit Categories";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            for (int i = 0; i < 5; i++) {
                msg = "Category "+(i+1)+": ";
                strWidth = g.getFontMetrics().stringWidth(msg);
                g.drawString(msg, (200-strWidth), 235+100*i);
                g.drawString(msg, (795-strWidth), 235+100*i);
            }
            font = new Font("Serif", Font.BOLD, 40);
            g.setFont(font);
            int strWidthADD = g.getFontMetrics().stringWidth("ADD");
            g.drawString("ADD", 200-strWidth+(540-(200-strWidth)-strWidthADD)/2, 150);
            int strWidthREM = g.getFontMetrics().stringWidth("REMOVE");
            g.drawString("REMOVE", 795-strWidth+(945-(795-strWidth)-strWidthREM)/2, 150);
            
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            strWidth = g.getFontMetrics().stringWidth("Name");
            g.drawString("Name", 205+(150-strWidth)/2, 190);
            strWidth = g.getFontMetrics().stringWidth("Symb.");
            g.drawString("Symb.", 360+(75-strWidth)/2, 190);
            strWidth = g.getFontMetrics().stringWidth("Budg.");
            g.drawString("Budg.", 440+(100-strWidth)/2, 190);
            strWidth = g.getFontMetrics().stringWidth("Name/Symbol");
            g.drawString("Name/Symbol", 800+(145-strWidth)/2, 190);
        }
    }
    
    class ExpRepPanel extends JPanel implements ActionListener
    {
        int expRepHeight = height; // ensure this is a multiple of height
        int numExpenses = 0;
        double totalCost = 0; // total amount spent across all relevant Purchases
        int curYear = 0; // current year
        JComboBox monthStart, dayStart, monthEnd, dayEnd, catBox, vendorBox;
        JTextField yearStart, yearEnd, minAmtField, maxAmtField;
        ArrayList<ArrayList<String>> expensesToShow = new ArrayList<ArrayList<String>>();
        ArrayList<JTextField> textFields = new ArrayList<JTextField>();
        
        public ExpRepPanel()
        {
            setLayout(null);
            
            setPreferredSize(new Dimension(width, expRepHeight)); // change vertical dimension
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(this);
            logoutBtn.setBounds(width-300, 50, 200, 50);
            add(logoutBtn);
            
            JButton showBtn = new JButton("Show");
            showBtn.addActionListener(this);
            showBtn.setBounds(width-300, 200, 200, 50);
            add(showBtn);
            
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(this);
            backBtn.setBounds(width-300, 300, 200, 50);
            add(backBtn);
            
            monthStart = new JComboBox(monthsList);
            monthStart.addActionListener(this);
            monthStart.setBounds(205, 160, 100, 30);
            
            add(monthStart);
            
            monthEnd = new JComboBox(monthsList);
            monthEnd.addActionListener(this);
            monthEnd.setBounds(605, 160, 100, 30);
            add(monthEnd);
            
            dayStart = new JComboBox();
            dayStart.addItem("Day");
            for (int i = 1; i <= 31; i++) {
                dayStart.addItem(""+i);
            }
            dayStart.addActionListener(this);
            dayStart.setBounds(310, 160, 50, 30);
            add(dayStart);
            
            dayEnd = new JComboBox();
            dayEnd.addItem("Day");
            for (int i = 1; i <= 31; i++) {
                dayEnd.addItem(""+i);
            }
            dayEnd.addActionListener(this);
            dayEnd.setBounds(710, 160, 50, 30);
            add(dayEnd);
            
            yearStart = new JTextField("");
            yearStart.setBounds(365, 160, 100, 30);
            yearStart.addActionListener(this);
            add(yearStart);
            
            yearEnd = new JTextField("");
            yearEnd.setBounds(765, 160, 100, 30);
            yearEnd.addActionListener(this);
            add(yearEnd);
            
            // items added to catBox and vendorBox from ProfilePanel class
            catBox = new JComboBox();
            /*catBox.addItem("ALL");
            for (int i = 0; i < userCategories.size(); i++) {
                catBox.addItem(userCategories.get(i).getName().toLowerCase());
            }*/
            catBox.setBounds(205, 195, 150, 30);
            catBox.addActionListener(this);
            add(catBox);
            
            vendorBox = new JComboBox();
            /*vendorBox.addItem("ALL");
            for (int i = 0; i < userVendors.size(); i++) {
                vendorBox.addItem(userVendors.get(i).toLowerCase());
            }*/
            vendorBox.setBounds(605, 195, 150, 30);
            vendorBox.addActionListener(this);
            add(vendorBox);
            
            LocalDate today = LocalDate.now();
            curYear = today.getYear();
            monthStart.setSelectedItem("January");
            dayStart.setSelectedItem("1");
            yearStart.setText(curYear+"");
            monthEnd.setSelectedItem("December");
            dayEnd.setSelectedItem("31");
            yearEnd.setText(curYear+"");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            int mStartIndex = monthStart.getSelectedIndex();
            String mStart = monthsList[mStartIndex];
            // note that if dStart = 0, then the value is "Day" (invalid for date format)
            int dStart = dayStart.getSelectedIndex();
            String yStart = yearStart.getText();
            int mEndIndex = monthEnd.getSelectedIndex();
            String mEnd = monthsList[mEndIndex];
            // note that if dEnd = 0, then the value is "Day" (invalid for date format)
            int dEnd = dayEnd.getSelectedIndex();
            String yEnd = yearEnd.getText();
            String catSelected = (String)(catBox.getSelectedItem());
            String vendorSelected = (String)(vendorBox.getSelectedItem());
            
            String command = e.getActionCommand();
            if (command.equals("Logout")) {
                clearExpRepInfo();
                for (int i = 0; i < textFields.size(); i++)
                    textFields.get(i).setVisible(false);
                card.show(ctr, "Welcome Panel");
            } else if (command.equals("Show")) {
                boolean datesAreValid = modifiedDateChecker(mStartIndex, dStart, yStart, mEndIndex, dEnd, yEnd);
                if (!datesAreValid) {
                    JOptionPane.showMessageDialog(frame, "Invalid start/end date(s).");
                } else {
                    totalCost = 0; // reset totalCost
                    expensesToShow.clear();
                    getRelevantExpenses(mStartIndex, dStart, Integer.parseInt(yStart), 
                            mEndIndex, dEnd, Integer.parseInt(yEnd), catSelected, vendorSelected);
                    numExpenses = expensesToShow.size();
                    /*
                    200 is the starting y-position of the first JTextField
                    40 is the height needed for JTextField to display information
                    */
                    expRepHeight = leastMultipleAbove(300 + numExpenses*40);
                    setPreferredSize(new Dimension(width, expRepHeight));
                    repaint();
                    for (int i = 0; i < textFields.size(); i++)
                        textFields.get(i).setVisible(false);
                    textFields.clear();
                    /*
                    We do 5*numExpenses since each entry requires 5 JTextFields
                    1. Date 2. Vendor 3. Description 4. Category (Symbol) 5. Amount
                    */
                    for (int i = 0; i < numExpenses; i++) {
                        ArrayList<String> tempList = expensesToShow.get(i);
                        JTextField dateField = new JTextField();
                        dateField.setBounds(50, 300+40*i, 80, 40);
                        dateField.setEditable(false);
                        dateField.setText(tempList.get(0));
                        dateField.setHorizontalAlignment(SwingConstants.CENTER);
                        textFields.add(dateField);

                        JTextField vendorField = new JTextField();
                        vendorField.setBounds(135, 300+40*i, 200, 40);
                        vendorField.setEditable(false);
                        vendorField.setText(tempList.get(1).toUpperCase());
                        vendorField.setHorizontalAlignment(SwingConstants.CENTER);
                        textFields.add(vendorField);

                        JTextField descField = new JTextField();
                        descField.setBounds(340, 300+40*i, 400, 40);
                        descField.setEditable(false);
                        descField.setText(tempList.get(2));
                        textFields.add(descField);

                        JTextField catField = new JTextField();
                        catField.setBounds(745, 300+40*i, 200, 40);
                        catField.setEditable(false);
                        catField.setText(tempList.get(3).toUpperCase());
                        catField.setHorizontalAlignment(SwingConstants.CENTER);
                        textFields.add(catField);

                        JTextField amtField = new JTextField();
                        amtField.setBounds(950, 300+40*i, 75, 40);
                        amtField.setEditable(false);
                        double amtAsDouble = Double.parseDouble(tempList.get(4));
                        amtField.setText(String.format("%.2f", amtAsDouble));
                        amtField.setHorizontalAlignment(SwingConstants.RIGHT);
                        textFields.add(amtField);
                        totalCost += amtAsDouble; // increment totalCost
                    }
                    for (int i = 0; i < textFields.size(); i++) {
                        add(textFields.get(i));
                    }
                    repaint();
                }
                
                // check if min/max amounts are valid --> later
                // count number of expenses to show and adjust expRepHeight, repaint()
                // show JTextFields
            } else if (command.equals("Back")) {
                clearExpRepInfo();
                for (int i = 0; i < textFields.size(); i++)
                    textFields.get(i).setVisible(false);
                card.show(ctr, "Profile Panel");
            }
        }
        
        /**
         * Gets the least multiple of "height" greater than or equal to "min"
         * This method is necessary because expRepHeight must be a multiple of "height"
         * @param min       Base number
         * @return          Least multiple >= min
         */
        public int leastMultipleAbove(int min)
        {
            if (min % height == 0)
                return min;
            int quotient = min/height;
            return (height*(quotient+1));
        }
        
        /**
         * Fills the expensesToShow list
         * Read from entriesList (ArrayList) and populate the expensesToShow
         * ArrayList with expenses that fall between the given start date 
         * (m1/d1/y1) and the given end date (m2/d2/y2) and fall under the specified
         * category (String cat) and vendor (String vendor). Note that cat and
         * vendor can have the value "ALL" in which all expenses must be counted.
         * @param m1        Start month
         * @param d1        Start day
         * @param y1        Start year
         * @param m2        End month
         * @param d2        End day
         * @param y2        End year
         * @param cat       Category    
         * @param vendor    Vendor
         */
        public void getRelevantExpenses(int m1, int d1, int y1, int m2, int d2, int y2, String cat, String vendor)
        {
            for (int i = 0; i < entriesList.size(); i++) {
                /* 
                Note that the .toLowerCase() makes "ALL" become "all" if
                applicable to vendor and cat 
                */
                vendor = vendor.toLowerCase();
                cat = cat.toLowerCase();
                ArrayList<String> entry = entriesList.get(i);
                ArrayList<String> entryCopy = new ArrayList<String>();
                for (int j = 0; j < entry.size(); j++)
                    entryCopy.add(entry.get(j));
                String entryDate = entry.get(0);
                String[] dateSplit = entryDate.split("/");
                int entryMonth = Integer.parseInt(dateSplit[0]);
                int entryDay = Integer.parseInt(dateSplit[1]);
                int entryYear = Integer.parseInt(dateSplit[2]);
                String entryVendor = entry.get(1).toLowerCase();
                String entryCat = entry.get(3).toUpperCase();
                // if entry date is on or after start date and end date is on or after entry date
                boolean entryIsStart = (entryMonth == m1 && entryDay == d1 && entryYear == y1);
                boolean entryAfterStart = givenIsAfterToday(entryMonth, entryDay, entryYear, m1, d1, y1);
                boolean endIsEntry = (entryMonth == m2 && entryDay == d2 && entryYear == y2);
                boolean endAfterEntry = givenIsAfterToday(m2, d2, y2, entryMonth, entryDay, entryYear);
                if ((entryIsStart || entryAfterStart) && (endIsEntry || endAfterEntry)) { 
                    /*
                    Index 3 of entry ArrayList is category symbol for corresponding expense
                    Get the category name associated with this symbol
                    Make the category name uppercase
                    */
                    entryCopy.set(3, getCatName(entryCat).toUpperCase());
                    if (cat.equals("all") && vendor.equals("all")) { // category & vendor are both "ALL"
                        expensesToShow.add(entryCopy);
                    } else if (cat.equals("all")) { // category is "ALL" but vendor is not
                        if (vendor.equals(entryVendor)) {
                            expensesToShow.add(entryCopy);
                        }
                    } else if (vendor.equals("all")) { // vendor is "ALL" but category is not
                        if(getCatSymbol(cat).equals(entryCat)) {
                            expensesToShow.add(entryCopy);
                        }
                    } else { // category and vendor are both specified (i.e. not "ALL")
                        if (getCatSymbol(cat).equals(entryCat) && vendor.equals(entryVendor)) {
                            expensesToShow.add(entryCopy);
                        }
                    }
                } else if (!endAfterEntry)
                    break;
                /* EXPLANATION FOR BREAK STATEMENT
                if end date is not after entry date --> entry date is after end date
                entries are in chronological order, meaning every subsequent entry
                will be after end date --> we can break the for loop here
                */
            }
        }
        
        /**
         * Gets symbol associated with given category
         * @param cat       Category
         * @return          Symbol
         */
        public String getCatSymbol(String cat)
        {
            Category c = new Category(cat, "", 0);
            int index = userCategories.indexOf(c);
            return userCategories.get(index).getSymbol().toUpperCase();
        }
        
        /**
         * Gets name associated with given category symbol
         * @param cat       Category symbol
         * @return          Name
         */
        public String getCatName(String cat)
        {
            Category c = new Category("", cat, 0);
            int index = userCategories.indexOf(c);
            try {
                return userCategories.get(index).getName().toUpperCase();
            } catch (Exception e) { // in the case of expense report showing an item whose category has been removed
                return "Undefined Category";
            }
        }
        
        /**
         * Checks if the given information forms a valid date
         * This method is modified from the isValidDate method in the ExpensePanel
         * class. This method does not check if the given date is after today's
         * date since that check is not needed for displaying the expense report.
         * Instead, it checks for valid date formatting and ensures that the start
         * date given is on or before the end date given.
         * @param m1        Start month
         * @param d1        Start day
         * @param y1        Start year
         * @param m2        End month
         * @param d2        End day
         * @param y2        End year
         * @return          True if dates are valid ... False if invalid
         */
        public boolean modifiedDateChecker(int m1, int d1, String y1, int m2, int d2, String y2)
        {
            /*
            Jan, Mar, May, Jul, Aug, Oct, Dec have 31 days
                These months have indices 1, 3, 5, 7, 8, 10, 12
            Apr, Jun, Sep, Nov have 30 days
                These months have indices 4, 6, 9, 11
            Feb has 28 days --> 29 days if leap year
                This month has index 2
            */
            // selected value(s) are "Month" and/or "Day" --> invalid date format
            if (m1 == 0 || d1 == 0 || m2 == 0 || d2 == 0)
                return false;
            int year1 = 0;
            int year2 = 0;
            try {
                year1 = Integer.parseInt(y1);
                year2 = Integer.parseInt(y2);
            } catch (Exception e) {
                return false;
            }
            // Given dates are invalid if start date is after end date
            if (givenIsAfterToday(m1, d1, year1, m2, d2, year2))
                return false;
            if ((m1 == 4 || m1 == 6 || m1 == 9 || m1 == 11) && d1 > 30)
                return false;
            if ((m2 == 4 || m2 == 6 || m2 == 9 || m2 == 11) && d2 > 30)
                return false;
            if (m1 == 2) {
                boolean isLeap = isLeapYear(year1);
                if (isLeap && d1 > 29)
                    return false;
                if (!isLeap && d1 > 28)
                    return false;
            }
            if (m2 == 2) {
                boolean isLeap = isLeapYear(year2);
                if (isLeap && d2 > 29)
                    return false;
                if (!isLeap && d2 > 28)
                    return false;
            }
            return true;
        }
        
        /**
         * Reset ExpRepPanel screen to default values
         */
        public void clearExpRepInfo()
        {
            monthStart.setSelectedItem("January");
            monthEnd.setSelectedItem("December");
            dayStart.setSelectedItem("1");
            dayEnd.setSelectedItem("31");
            yearStart.setText(""+curYear);
            yearEnd.setText(""+curYear);
            catBox.setSelectedItem("ALL");
            vendorBox.setSelectedItem("ALL");
            numExpenses = 0;
            totalCost = 0;
        }
        
        // paintComponent method for ExpRepPanel class
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            int max = expRepHeight; // vertical dimension for panel
            for (int turn = 0; turn < max/height; turn++) {
                if (turn % 2 == 0) {
                    for (int i = 0; i < height; i++) {
                        g.setColor(new Color(135, 213, (int)(color - (i*255/height))));
                        g.drawLine(0, turn*height+i, width, turn*height+i);
                    }
                } else {
                    for (int i = 0; i < height; i++) {
                        g.setColor(new Color(135, 213, (int)((i*255/height))));
                        g.drawLine(0, turn*height+i, width, turn*height+i);
                    }
                }
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < expRepHeight; i++) {
                g.drawLine(0, i, width, i);
            }*/
            
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Expense Report";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("SansSerif", Font.BOLD, 30);
            g.setFont(font);
            strWidth = g.getFontMetrics().stringWidth("Start:");
            g.drawString("Start:", 200-strWidth, 185);
            strWidth = g.getFontMetrics().stringWidth("End:");
            g.drawString("End:", 600-strWidth, 185);
            strWidth = g.getFontMetrics().stringWidth("Category:");
            g.drawString("Category:", 200-strWidth, 220);
            strWidth = g.getFontMetrics().stringWidth("Vendor:");
            g.drawString("Vendor:", 600-strWidth, 220);
            font = new Font("SansSerif", Font.BOLD, 20);
            g.setFont(font);
            msg = "Amount spent: $"+String.format("%.2f",totalCost);
            strWidth = g.getFontMetrics().stringWidth(msg);
            // align Strings with the JButtons (i.e. x-position is width-300)
            g.drawString(msg, width-300, 500);
            msg = "Expense entries: "+numExpenses;
            g.drawString(msg, width-300, 550);
            /*
            Draw column headers --> Date, Vendor, Description, Category, Amount
            Column headers are 10 units above the top of JTextFields for expense information
            Column headers are centered with respect to their JTextFields
            x-position for column header = x0 + (x1-x0-strWidth)/2
                x0 = start x-position for JTextField
                x1 = end x-position for JTextField
            */
            if (numExpenses > 0) {
                strWidth = g.getFontMetrics().stringWidth("Date");
                g.drawString("Date", 50+(130-50-strWidth)/2, 290);
                strWidth = g.getFontMetrics().stringWidth("Vendor");
                g.drawString("Vendor", 135+(335-135-strWidth)/2, 290);
                strWidth = g.getFontMetrics().stringWidth("Description");
                g.drawString("Description", 340+(740-340-strWidth)/2, 290);
                strWidth = g.getFontMetrics().stringWidth("Category");
                g.drawString("Category", 745+(945-745-strWidth)/2, 290);
                strWidth = g.getFontMetrics().stringWidth("Amount");
                g.drawString("Amount", 950+(1025-950-strWidth)/2, 290);
            } else {
                msg = "Nothing to see here!";
                strWidth = g.getFontMetrics().stringWidth(msg);
                g.drawString(msg, (width-strWidth)/2, 290);
            }
        }
    }
    
    class VendorPanel extends JPanel implements ActionListener
    {
        JTextField[] addVendorFields = new JTextField[5];
        JComboBox[] remVendorBoxes = new JComboBox[5];
        
        public VendorPanel()
        {
            setLayout(null);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(this);
            logoutBtn.setBounds(width-300, 50, 200, 50);
            add(logoutBtn);
            
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(this);
            clearBtn.setBounds(width-300, 200, 200, 50);
            add(clearBtn);
            
            JButton saveBtn = new JButton("Save"); 
            saveBtn.addActionListener(this);
            saveBtn.setBounds(width-300, 300, 200, 50);
            add(saveBtn);
            
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(this);
            backBtn.setBounds(width-300, 400, 200, 50);
            add(backBtn);
            
            for (int i = 0; i < 5; i++) {
                addVendorFields[i] = new JTextField("");
                addVendorFields[i].addActionListener(this);
                addVendorFields[i].setBounds(205, 200+100*i, 200, 50);
                addVendorFields[i].setHorizontalAlignment(JTextField.CENTER); // enter text from the center
                add(addVendorFields[i]);
                
                remVendorBoxes[i] = new JComboBox();
                remVendorBoxes[i].addItem("Select a Vendor");
                for (int j = 0; j < userVendors.size(); j++)
                    remVendorBoxes[i].addItem(userVendors.get(j));
                remVendorBoxes[i].addActionListener(this);
                remVendorBoxes[i].setBounds(805, 200+100*i, 200, 50); 
                add(remVendorBoxes[i]);
            }
        }
        
        public void actionPerformed(ActionEvent e)
        {
            // first 5 values represent added vendors, next 5 represent removed vendors
            boolean[] indicesToClear = new boolean[10];
            String command = e.getActionCommand();
            if (command.equals("Logout")) {
                for (int i = 0; i < 10; i++)
                    indicesToClear[i] = true;
                clearVendorInfo(indicesToClear);
                card.show(ctr, "Welcome Panel");
            } else if (command.equals("Clear")) {
                for (int i = 0; i < 10; i++)
                    indicesToClear[i] = true;
                clearVendorInfo(indicesToClear);
            } else if (command.equals("Save")) {
                indicesToClear = saveVendorChanges();
                clearVendorInfo(indicesToClear);
            } else if (command.equals("Back")) {
                for (int i = 0; i < 10; i++)
                    indicesToClear[i] = true;
                clearVendorInfo(indicesToClear);
                card.show(ctr, "Profile Panel");
            }
        }
        
        public boolean[] saveVendorChanges()
        {
            boolean[] clearData = new boolean[10];
            // for adding vendors
            String popupMsg = "";
            for (int i = 0; i < 5; i++) {
                popupMsg += ("ADD Vendor "+(i+1)+": ");
                String vendorGiven = addVendorFields[i].getText().toLowerCase();
                String catMsg = getVendorMsgAdd(vendorGiven);
                popupMsg += catMsg;
                if (catMsg.equals("Added!")) {
                    clearData[i] = true; // if vendor is saved, then we can clear the JTextField (i.e. don't save on screen)
                    appendVendor(vendorGiven);
                    
                } 
                popupMsg += "\n";
            }
            userVendors.clear();
            fillVendorsList("vendors - "+currentUsername+".txt");
            JOptionPane.showMessageDialog(frame, popupMsg);
            // Remove categories
            popupMsg = "";
            for (int i = 0; i < 5; i++) {
                popupMsg += ("REM Category "+(i+1)+": ");
                String vendorGiven = (String)(remVendorBoxes[i].getSelectedItem());
                if (vendorGiven.equals("Select a Vendor"))
                    popupMsg += "Empty";
                else {
                    vendorGiven = vendorGiven.toLowerCase();
                    int index = userVendors.indexOf(vendorGiven);
                    userVendors.remove(index);
                    rewriteVendors("vendors - "+currentUsername+".txt");
                    popupMsg += "Removed!";
                    clearData[i+5] = true;
                }
                popupMsg += "\n";
            }
            for (int i = 0; i < 5; i++) {
                remVendorBoxes[i].removeAllItems();
                remVendorBoxes[i].addItem("Select a Vendor");
                for (int j = 0; j < userVendors.size(); j++)
                    remVendorBoxes[i].addItem(userVendors.get(j));
            }
            JOptionPane.showMessageDialog(frame, popupMsg);
            
            return clearData;
        }
        
        /**
         * Updates the vendors txt file following a removal
         * @param fileName      File name of txt file
         */
        public void rewriteVendors(String fileName)
        {
            String folderName = currentUsername + " - Info";
            File file = new File(mainFolderName+File.separator+folderName+File.separator+fileName);
            FileWriter fw = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(file);
                pw = new PrintWriter(fw);
                for (int i = 0; i < userVendors.size(); i++) {
                    pw.println(userVendors.get(i).toLowerCase());
                }                
            } catch (Exception e) {
                System.err.println("Error with File Writing");
                System.exit(7);
            }
            pw.close();
        }
        
        /**
         * Resets data on the VendorPanel screen for vendors that have just been saved successfully
         * @param arr       boolean array of indices to clear
         */
        public void clearVendorInfo(boolean[] arr)
        {
            for (int i = 0; i < 10; i++) {
                if (i < 5 && arr[i]) {
                    addVendorFields[i].setText("");
                } 
                if (i >= 5 && arr[i]) {
                    remVendorBoxes[i-5].setSelectedItem("Select a Vendor");
                }
            }
        }
        
        /**
         * Get the associated message for given vendor
         * @param vendor        Vendor name given
         * @return              Associated message
         */
        public String getVendorMsgAdd(String vendor)
        {
            // no commas should be in vendor name and vendor length must be > 0
            if (vendor.indexOf(",") != -1)
                return "Invalid vendor";
            if (vendor.length() == 0)
                return "Empty";
            if (userVendors.contains(vendor))
                return "Vendor already exists";
            return "Added!"; // vendor name is good and can be added
        }
        
        // paintComponent method for VendorPanel class
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            for (int i = 0; i < height; i++) {
                g.setColor(new Color((int)(color - (i*255/height)), 156, 105));
                g.drawLine(0, i, width, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Edit Vendors";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            for (int i = 0; i < 5; i++) {
                msg = "Category "+(i+1)+": ";
                strWidth = g.getFontMetrics().stringWidth(msg);
                g.drawString(msg, (200-strWidth), 235+100*i);
                g.drawString(msg, (800-strWidth), 235+100*i);
            }
            font = new Font("Serif", Font.BOLD, 40);
            g.setFont(font);
            int strWidthADD = g.getFontMetrics().stringWidth("ADD");
            g.drawString("ADD", 200-strWidth+(405-(200-strWidth)-strWidthADD)/2, 170);
            int strWidthREM = g.getFontMetrics().stringWidth("REMOVE");
            g.drawString("REMOVE", 800-strWidth+(1005-(800-strWidth)-strWidthREM)/2, 170);
        }
    }
    
    class EditCategoryPanel extends JPanel implements ActionListener
    {
        JComboBox[] catNames;
        JTextField[] catSymbs, catBudgets;
        
        public EditCategoryPanel()
        {
            setLayout(null);
            
            catNames = new JComboBox[10];
            catSymbs = new JTextField[10];
            catBudgets = new JTextField[10];
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.addActionListener(this);
            logoutBtn.setBounds(width-250, 50, 200, 50);
            add(logoutBtn);
            
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(this);
            clearBtn.setBounds(width-250, 200, 200, 50);
            add(clearBtn);
            
            JButton saveBtn = new JButton("Save"); 
            saveBtn.addActionListener(this);
            saveBtn.setBounds(width-250, 300, 200, 50);
            add(saveBtn);
            
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(this);
            backBtn.setBounds(width-250, 400, 200, 50);
            add(backBtn);
                        
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 2; j++) {
                    // add items for catNames when button is pressed to get to Edit Category Panel
                    int index = 2*i+j;
                    catNames[index] = new JComboBox();
                    catNames[index].addItem("Select a Category");
                    catNames[index].addActionListener(this);
                    catNames[index].setBounds(205+520*j, 200+100*i, 150, 50);
                    add(catNames[index]);

                    catSymbs[index] = new JTextField("");
                    catSymbs[index].addActionListener(this);
                    catSymbs[index].setBounds(360+520*j, 200+100*i, 75, 50);
                    catSymbs[index].setHorizontalAlignment(JTextField.CENTER); // enter text from the center
                    add(catSymbs[index]);

                    catBudgets[index] = new JTextField("");
                    catBudgets[index].addActionListener(this);
                    catBudgets[index].setBounds(440+520*j, 200+100*i, 100, 50);
                    catBudgets[index].setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // enter text from the right
                    add(catBudgets[index]);
                }
            }
        }
        
        public void actionPerformed(ActionEvent e)
        {
            boolean[] toClear = new boolean[10];
            String command = e.getActionCommand();
            // if the quantity passed to clearScreenInfo is {true, ..., true}, then we clear whole screen
            if (command.equals("Logout")) {
                for (int i = 0; i < 10; i++)
                    toClear[i] = true;
                clearScreenInfo(toClear);
                card.show(ctr, "Welcome Panel");
            } else if (command.equals("Clear")) {
                for (int i = 0; i < 10; i++)
                    toClear[i] = true;
                clearScreenInfo(toClear);
            } else if (command.equals("Save")) {
                toClear = saveCatEdits();
                clearScreenInfo(toClear);
                rewriteCategories("categories - "+currentUsername+".csv");
            } else if (command.equals("Back")) {
                for (int i = 0; i < 10; i++)
                    toClear[i] = true;
                clearScreenInfo(toClear);
                card.show(ctr, "Profile Panel");
            }
        }
        
        /**
         * Saves any category edits
         * @return      Boolean array for which the element is true if the 
         */
        public boolean[] saveCatEdits() 
        {
            boolean[] clearArr = new boolean[10];
            // Add categories
            String popupMsg = "";
            for (int i = 0; i < 10; i++) {
                popupMsg += ("EDIT Category "+(i+1)+": ");
                String givenName = (String)(catNames[i].getSelectedItem());
                String givenSymbol = catSymbs[i].getText();
                String givenBudget = catBudgets[i].getText();
                String catMsg = getCatMsgEdit(givenName, givenSymbol, givenBudget);
                if (catMsg.equals("Default")) {
                    popupMsg += "Empty";
                } else if (catMsg.equals("Bad symbol")) {
                    popupMsg += ("Invalid symbol.");
                } else if (catMsg.equals("Bad budget")) {
                    popupMsg += ("Invalid budget.");
                } else if (catMsg.equals("Negative budget")) {
                    popupMsg += ("Please enter a nonnegative budget.");
                } else if (catMsg.equals("Good")) {
                    Category cat = new Category(givenName, "", 0); // general category with givenName
                    int index = userCategories.indexOf(cat); // index of general category
                    Category realCat = userCategories.get(index); // actual category stored at index
                    
                    // nothing entered for symbol & budget --> retain default/previous symbol & budget
                    if (givenSymbol.length() == 0 && givenBudget.length() == 0)
                        popupMsg += "No new changes to be made";
                    else {
                        popupMsg += ("Edited!");
                        // only symbol is empty --> need to change budget
                        if (givenSymbol.length() == 0) {
                            if (givenBudget.charAt(0) == '$')
                                givenBudget = givenBudget.substring(1);
                            realCat.setBudget(givenBudget);
                        } else if (givenBudget.length() == 0) { // only budget is empty --> need to change symbol
                            realCat.setSymbol(givenSymbol.toUpperCase());
                        } else { // change budget and symbol
                            if (givenBudget.charAt(0) == '$')
                                givenBudget = givenBudget.substring(1);
                            realCat.setBudget(givenBudget);
                            realCat.setSymbol(givenSymbol.toUpperCase());
                        }
                    }
                    clearArr[i] = true; // if category is saved, then we can clear it (i.e. don't save on screen)
                    
                } 
                popupMsg += "\n";
            }
            
            JOptionPane.showMessageDialog(frame, popupMsg);
            return clearArr;
        }
        
        /**
         * Get associated message with given category information to edit
         * @param name      Category name selected
         * @param symbol    New symbol given
         * @param budget    New budget given
         * @return          Associated message
         */
        public String getCatMsgEdit(String name, String symbol, String budget)
        {
            // nothing is entered --> default value
            if (name.equals("Select a Category") && symbol.length() == 0 && budget.length() == 0)
                return "Default";
            /* 
            symbol length must be between 1 and 5 chars (inclusive), but if
            a symbol of length 0 is entered, treat it as if user does not want to 
            change symbol
            */
            // symbol must not contain commas
            if (symbol.length() > 5 || symbol.contains(","))
                return "Bad symbol";
            // if budget length is 0, treat it as if user does not want to change budget
            double budgetDbl = 0;
            if (budget.length() > 0) {
                if (budget.charAt(0) == '$')
                    budget = budget.substring(1);
                try {
                    budgetDbl = Double.parseDouble(budget);
                    if (budgetDbl < 0)
                        return "Negative budget";
                } catch (Exception e) {
                    return "Bad budget";
                }
            }
            return "Good";
            
        }
        
        /**
         * Clears the relevant information from the screen
         * @param arr       If arr[i] is true, then clear the information
         */
        public void clearScreenInfo(boolean[] arr)
        {
            for (int i = 0; i < 10; i++) {
                if (arr[i]) {
                    catNames[i].setSelectedItem("Select a Category");
                    catSymbs[i].setText("");
                    catBudgets[i].setText("");
                }
            }
        }
        
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            for (int i = 0; i < height; i++) {
                g.setColor(new Color((int)(color - (i*255/height)), (int)(color - (i*255/height)), 205));
                g.drawLine(0, i, width, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < height; i++) {
                g.drawLine(0, i, width, i);
            }*/
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "Edit Category";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 2; j++) {
                    msg = "Category "+(2*i+j+1)+": ";
                    strWidth = g.getFontMetrics().stringWidth(msg);
                    g.drawString(msg, (200-strWidth)+520*j, 235+100*i);
                }
            }
            for (int i = 0; i < 2; i++) {
                strWidth = g.getFontMetrics().stringWidth("Symb.");
                g.drawString("Symb.", (360+(75-strWidth)/2) + 520*i, 190);
                strWidth = g.getFontMetrics().stringWidth("Budg.");
                g.drawString("Budg.", (440+(100-strWidth)/2) + 520*i, 190);
            }
            
        }
    }
    
    class ViewSummaryPanel extends JPanel implements ActionListener
    {
        int panelWidth = 0;
        int panelHeight = 0;
        JComboBox yearBox;
        String yearChosen;
        String prevYear;
        JTextField[][] fields;
        
        public ViewSummaryPanel()
        {
            setLayout(null);
            setPanelDimensions(Math.max(20 + 150 + 75*4 + 100*12 + 50, width), Math.max(100+userCategories.size()*40, height));
            // panel dimensions changed when the panel is shown (see profile panel class)
            yearBox = new JComboBox();
            yearBox.addActionListener(this);
            yearBox.setBounds(20, 50, 100, 40);
            add(yearBox);
            
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(this);
            backBtn.setBounds(width-250, 30, 200, 50);
            add(backBtn);
            
            setFieldsDefault(userCategories.size()+1, 17);
        }
        
        public void setYearBox()
        {
            // do nothing if entriesList has no entries
            yearBox.removeAllItems();
            if (entriesList.size() == 0) {
                yearBox.addItem("Year");
                yearChosen = "Year";
                prevYear = "";
            } else { // following takes place if entriesList.size() > 0
                ArrayList<String> firstEntry = entriesList.get(0);
                String firstDate = firstEntry.get(0);
                String[] split = firstDate.split("/");
                int firstYear = Integer.parseInt(split[2]);
                LocalDate today = LocalDate.now();
                int curYear = today.getYear();

                for (int i = firstYear; i <= curYear; i++)
                    yearBox.addItem(i+"");
                yearBox.setSelectedItem(curYear+"");
                yearChosen = curYear+"";
                prevYear = "";
            }
        }
        
        /**
         * Sets panel dimensions based on spacing of JTextFields
         * To see spacing, look at setTextFields() method
         */
        public void setDefaultPanelDim()
        {
            /*
            Space from left = 20
            Category field = 150, Symbol/Budget/Total/Average fields = 75
            Month fields = 100
            Space from rigtht = 50
            */
            panelWidth = Math.max(20 + 150 + 75*4 + 100*12 + 50, width);
            /*
            Space from top = 100
            Height for each label = 40
            Space from botton = 50;
            */
            panelHeight = Math.max(100 + userCategories.size()*40 + 50, height);
            // if there are no categories, there is nothing to display
            if (userCategories.size() == 0) {
                panelHeight = height;
            }
            setPreferredSize(new Dimension(panelWidth, panelHeight));
        }
        
        /**
         * Sets panel dimensions based on specified values
         * @param w     Width
         * @param h     Height
         */
        public void setPanelDimensions(int w, int h)
        {
            panelWidth = w;
            panelHeight = h;
            setPreferredSize(new Dimension(panelWidth, panelHeight));
        }
        
        /**
         * Sets the positioning and number of JTextFields
         * @param rows      Number of rows for JTextFields
         * @param cols      Number of columns for JTextFields
         * There are a total of rows*cols JTextFields
         */
        public void setFieldsDefault(int rows, int cols)
        {
            int fieldWidth = 0;
            fields = new JTextField[rows][cols];
            for (int i = 0; i < rows; i++) {
                int sumWidth = 20;
                for (int j = 0; j < cols; j++) {
                    fields[i][j] = new JTextField();
                    if (j == 0) {
                        fieldWidth = 150;
                    } else if (j == 1 || j == 2 || j == 15 || j == 16) {
                        fieldWidth = 75;
                    } else if (j >= 3 || j <= 14) {
                        fieldWidth = 100;
                    }
                    fields[i][j].setBounds(sumWidth, 100+40*i, fieldWidth, 40);
                    fields[i][j].setEditable(false);
                    add(fields[i][j]);
                    sumWidth += fieldWidth;
                }
            }
        }
        
        /**
         * Sets the JTextFields text
         */
        public void setTextFields()
        {
            for (int i = 0; i < fields.length; i++) {
                for (int j = 0; j < fields[0].length; j++) {
                    fields[i][j].setVisible(false);
                }
            }
            setFieldsDefault(userCategories.size()+1, 17);
            // need 17 columns: category, symbol, budget, 12 months, total, average
            String text = "";
            double[][] amtTotals = getTotals();
            LocalDate today = LocalDate.now();
            String monthNow = today.getMonth().toString();
            int monthInd = 0;
            for (int i = 0; i < monthsList.length; i++) {
                if (monthNow.equalsIgnoreCase(monthsList[i])) {
                    monthInd = i;
                    break;
                }
            }
            String yearNow = today.getYear()+"";
            // if the chosen year does not equal the current year, it must be in the past --> 12 months have passed
            if (!yearChosen.equals(yearNow))
                monthInd = 12;
            for (int i = 0; i < 17; i++) {
                if (i == 0) {
                    text = "Category";
                } else if (i == 1) {
                    text = "Symbol";
                } else if (i == 2) {
                    text = "Budget";
                } else if (i >= 3 && i <= 14) {
                    text = monthsList[i-3];
                } else if (i == 15) {
                    text = "Total";
                } else if (i == 16) {
                    text = "Average";
                }
                fields[0][i].setText(text);
            }
            text = "";
            // Start from i = 1 to account for header row
            for (int i = 1; i <= userCategories.size(); i++) {
                Category c = userCategories.get(i-1); 
                double yearTotal = getCatTotal(i-1, amtTotals);
                for (int j = 0; j < 17; j++) {
                    if (j == 0) {
                        text = c.getName().toUpperCase();
                    } else if (j == 1 || j == 2 || j == 15 || j == 16) {
                        if (j == 1)
                            text = c.getSymbol().toUpperCase();
                        else if (j == 2)
                            text = String.format("%.2f", c.getBudget());
                        else if (j == 15)
                            text = String.format("%.2f", yearTotal); // get total for that category for that year
                        else if (j == 16)
                            text = String.format("%.2f", yearTotal/monthInd); // get average for that category for the time that has elapsed in the year
                    } else if (j >= 3 || j <= 14) {
                        // subtract 3 from j to get the index in the 0-11 range
                        text = String.format("%.2f", amtTotals[i-1][j-3]); // get total for that category for that month
                    }
                    fields[i][j].setText(text);
                }
            }
        }
        
        /**
         * Get total amount spent on a certain category over the year
         * @param index     Index of category
         * @param arr       Array of categories and amounts spent by month
         * @return          Total amount spent in a year
         */
        public double getCatTotal(int index, double[][] arr)
        {
            double sum = 0;
            for (int i = 0; i < arr[index].length; i++) {
                sum += arr[index][i];
            }
            return sum;
        }
        
        /**
         * Get amounts spent per category by month
         * @return      double[][] with userCategories.size() rows and 12 columns (1 for each month)
         */
        public double[][] getTotals()
        {
            // one row for each category, one column for each month (12 total)
            double[][] totals = new double[userCategories.size()][12];
            // if the year chosen to display is default, return an empty array
            if (yearChosen.equals("Year"))
                return totals;
            for (int i = 0; i < entriesList.size(); i++) {
                ArrayList<String> entry = entriesList.get(i);
                String date = entry.get(0);
                String[] dateSplit = date.split("/");
                int dateMonth = Integer.parseInt(dateSplit[0]);
                String dateYear = dateSplit[2];
                
                if (dateYear.equals(yearChosen)) {
                    String catSymbol = entry.get(3).toUpperCase();
                    Category cat = new Category("", catSymbol, 0);
                    int index = userCategories.indexOf(cat);
                    double amt = Double.parseDouble(entry.get(4));

                    if (index != -1)
                        totals[index][dateMonth-1] += amt;
                    
                }
            }
            
            return totals;
        }
        
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            yearChosen = (String)(yearBox.getSelectedItem());
            LocalDate today = LocalDate.now();
            int curYear = today.getYear();
            // if the yearChosen is null, then set it to the current year
            if (yearChosen == null)
                yearChosen = curYear+"";
            
            // if the year chosen does not change, there is no need to do anything
            // only reset text fields if the year chosen changes
            if (!yearChosen.equals(prevYear))
                setTextFields();
            prevYear = yearChosen;
            
            if (command.equals("Back")) {
                card.show(ctr, "Profile Panel");
            }
        }
        
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            double color = 255.0; // base color value
            for (int i = 0; i < panelHeight; i++) {
                g.setColor(new Color((int)(color - (i*255/height)), 184, (int)(color - (i*255/height))));
                g.drawLine(0, i, panelWidth, i);
            }
            /*g.setColor(new Color(35, 213, 255));
            for (int i = 0; i < panelHeight; i++) {
                g.drawLine(0, i, panelWidth, i);
            }*/
            g.setColor(Color.BLACK);
            Font font = new Font("SansSerif", Font.BOLD + Font.ITALIC, 50);
            g.setFont(font);
            String msg = "View Summary";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width-strWidth)/2, 75);
            font = new Font("Serif", Font.BOLD, 30);
            g.setFont(font);
        }
    }
    
    class Category
    {
        private String name, symbol, budgStr;
        private double budget;
        
        public Category()
        {
            name = symbol = budgStr = ""; budget = 0;
        }
        
        public Category(String n, String s, String b)
        {
            name = n.toLowerCase(); symbol = s.toUpperCase(); budgStr = b; 
            if (b.charAt(0) == '$') 
                b = b.substring(1);
            if (b.equals(""))
                budget = 0;
            else
                budget = Double.parseDouble(b);
        }
        
        public Category (String n, String s, double b)
        {
            name = n.toLowerCase(); symbol = s.toUpperCase(); budget = b;
        }
        
        public String getName() {return name;}
        public String getSymbol() {return symbol;}
        public double getBudget() {return budget;}
        
        public void setName(String n) {name = n.toLowerCase();}
        public void setSymbol(String s) {symbol = s.toUpperCase();}
        public void setBudget(String b) {
            budgStr = b; 
            try {
                budget = Double.parseDouble(b);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid budget value. Value defaults to $0");
                budget = 0;
            }
        }
        public void setBudget(double b) {budget = b;}
        
        public boolean sharesName(Category other)
        {
            return (name.equals(other.name));
        }
        
        public boolean sharesSymbol(Category other)
        {
            return (symbol.equals(other.symbol));
        }
        
        public boolean equals(Object o)
        {
            if (!(o instanceof Category))
                return false;
            Category other = (Category)(o);
            return (name.equals(other.name) || symbol.equals(other.symbol));
        }
        
        public String toString()
        {
            return (name + " ("+symbol+"): $"+String.format("%.2f", budget));
        }
    }
}
