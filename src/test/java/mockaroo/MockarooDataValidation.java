package mockaroo;
	
	import java.io.BufferedReader;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.HashSet;
	import java.util.Iterator;
	import java.util.List;
	import java.util.Set;
	import java.util.concurrent.TimeUnit;
	import org.openqa.selenium.By;
	import org.openqa.selenium.Keys;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.openqa.selenium.support.ui.Select;
	import static org.testng.Assert.*;
	import org.testng.annotations.AfterClass;
	import org.testng.annotations.BeforeClass;
	import org.testng.annotations.Test;
	import io.github.bonigarcia.wdm.WebDriverManager;
	public class MockarooDataValidation {
	  WebDriver driver;
	  List<String> cities;
	  List<String> countries;
	  Set<String> citiesSet;
	  Set<String> countriesSet;
	  
	  int lineCount;
	  @BeforeClass
	  public void setUp() {
	    WebDriverManager.chromedriver().setup();
	    driver = new ChromeDriver();
	    cities = new ArrayList();
	    countriesSet= new HashSet<>();
	    citiesSet=new HashSet<>();
	    countries = new ArrayList();
	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    driver.manage().window().maximize();
	    String url = "https://mockaroo.com/";
	    driver.get(url);
	  }
	  @Test(priority = 1)
	  public void verifyTitle() {
	    String actual = driver.getTitle();
	    String expected = "Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel";
	    assertEquals(actual, expected);
	  }
	  @Test(priority = 2)
	  public void verifyHeader() {
	    String actual = driver.findElement(By.xpath("//a[@href='/']/div[1]")).getText();
	    String expected = "mockaroo";
	    assertEquals(actual, expected);
	    actual = driver.findElement(By.xpath("//a[@href='/']/div[2]")).getText();
	    expected = "realistic data generator";
	    assertEquals(actual, expected);
	    removeExistingFields();
	  }
	  @Test(priority = 3)
	  public void verifyTableHeader() {
	    assertTrue(verifyStrings());
	    assertTrue(driver.findElement(By.xpath("//a[.='Add another field']")).isEnabled());
	    // isEnabled() checks for the disabled attribute on the button element. If the
	    // attribute "disabled" is not present, it returns True.
	    String actual = driver.findElement(By.cssSelector("input[id='num_rows']")).getAttribute("value");
	    assertEquals(actual, "1000");
	    Select select = new Select(driver.findElement(By.cssSelector("select.form-control#schema_file_format")));
	    actual = select.getFirstSelectedOption().getText();
	    String expected = "CSV";
	    assertEquals(actual, expected);
	    select = new Select(driver.findElement(By.cssSelector("select.form-control#schema_line_ending")));
	    actual = select.getFirstSelectedOption().getText();
	    expected = "Unix (LF)";
	    assertEquals(actual, expected);
	    assertTrue(driver.findElement(By.xpath("//input[@name='schema[include_header]'][@value='1']")).isSelected());
	    assertFalse(driver.findElement(By.xpath("//input[@name='schema[bom]'][@value='1']")).isSelected());
	  }
	  @Test(priority = 4)
	  public void creatingFields() throws InterruptedException {
	    driver.findElement(By.xpath("//a[.='Add another field']")).click();
	    driver.findElement(By.xpath("(//div[@class='column']//input[@placeholder='enter name...'])[7]"))
	        .sendKeys("City");
	    driver.findElement(By.xpath("(//input[@class='btn btn-default'])[7]")).click();
	    Thread.sleep(1000);
	    assertEquals("Choose a Type",
	        driver.findElement(By.xpath("//h3[@class='modal-title'][.='Choose a Type']")).getText());
	    driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("city");
	    driver.findElement(By.xpath("//div[@class='examples']")).click();
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']")).click();
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("(//input[@class='column-name form-control'])[8]")).sendKeys("Country");
	    driver.findElement(By.xpath("(//input[@class='btn btn-default'])[8]")).click();
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("(//input[@id='type_search_field'])")).clear();
	    driver.findElement(By.xpath("(//input[@id='type_search_field'])")).sendKeys("country");
	    driver.findElement(By.xpath("//div[.='Country']")).click();
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("//button[@class='btn btn-success']")).click(); // clicks download
	  }
	  @Test (priority=5)
	  public void verifyDownloadedData() throws IOException {
	    loadLists();
	    assertEquals(lineCount, 1000);
	    sortCities();
	    findCountries();
	    loadSets();
	    
	    int actual = removeDuplicates(cities);
	    assertEquals(actual, citiesSet.size());
	    
	    actual=removeDuplicates(countries);
	    
	    assertEquals(actual, countriesSet.size());
	    
	  }
	  @AfterClass
	  public void tearDown() {
	     driver.close();
	  }
	  public void removeExistingFields() {
	    List<WebElement> fields = driver
	        .findElements(By.xpath("//a[@class='close remove-field remove_nested_fields']"));
	    for (WebElement webElement : fields) {
	      webElement.click();
	    }
	  }
	  public boolean verifyStrings() {
	    List<WebElement> actual = driver.findElements(By.xpath("//div[@class='table-header']//div"));
	    List<String> expected = new ArrayList<>();
	    expected.add("Field Name");
	    expected.add("Type");
	    expected.add("Options");
	    String eachActual;
	    for (int i = 0; i < actual.size(); i++) {
	      eachActual = actual.get(i).getText();
	      if (!eachActual.equals(expected.get(i))) {
	        return false;
	      }
	    }
	    return true;
	  }
	  public void sortCities() {
	    Collections.sort(cities);
	    int max = cities.get(0).length();
	    for (String string : cities) {
	      if (string.length() > max)
	        max = string.length();
	    }
	    int min = cities.get(0).length();
	    for (String string : cities) {
	      if (string.length() < min)
	        min = string.length();
	    }
	    System.out.println("City-Name: Maximum Length is " + max);
	    System.out.println("City_Name: Minimum Length is " + min);
	  }
	  public void findCountries() {
	    int count = 0;
	    Set<String> k = new HashSet<>(countries);
	    for (String outer : k) {
	      for (String inner : countries) {
	        if (inner.equals(outer))
	          count++;
	      }
	      System.out.println(outer + "-" + count);
	      count = 0;
	    }
	  }
	  // public int uniqueCities() {
	  //
	  // citiesSet= new HashSet(cities);
	  // int count=0;
	  // int numberOfUniqueCities=0;
	  //
	  // for (String outer : citiesSet) {
	  //
	  // for (String inner : cities) {
	  // if (inner.equals(outer))
	  // count++;
	  // }
	  // if(count==1) {
	  // numberOfUniqueCities++;
	  // }
	  // count = 0;
	  // }
	  //
	  // return numberOfUniqueCities;
	  // }
	  public void loadLists() throws IOException {
	    FileReader reader = new FileReader("C:/Users/obeyd/Downloads/MOCK_DATA.csv");
	    BufferedReader breader = new BufferedReader(reader);
	    String temp = breader.readLine();
	    assertEquals(temp, "City,Country");
	    lineCount = 0;
	    temp = breader.readLine();
	    String[] something = new String[2];
	    while (temp != null) {
	      something = temp.split(",");
	      cities.add(something[0]);
	      countries.add(something[1]);
	      lineCount++;
	      temp = breader.readLine();
	      
	    }
	      reader.close();
	      breader.close();
	  }
	  
	  public void loadSets() throws IOException {
	    
	    FileReader reader = new FileReader("C:/Users/obeyd/Downloads/MOCK_DATA.csv");
	    BufferedReader breader = new BufferedReader(reader);
	    String temp = breader.readLine();
	    temp = breader.readLine();
	    String[] something = new String[2];
	    while (temp != null) {
	      something = temp.split(",");
	      citiesSet.add(something[0]);
	      countriesSet.add(something[1]);
	      lineCount++;
	      temp = breader.readLine();
	  }
	    
	    reader.close();
	    breader.close();
	}
	  public int removeDuplicates(List<String> myList) {
	    Iterator<String> myIterator = myList.iterator();
	    List<String> localList = new ArrayList<>();
	    String each = new String();
	    boolean add = true;
	    while (myIterator.hasNext()) {
	      each = myIterator.next();
	      add = true;
	      for (String string : localList) {
	        if (each.equals(string)) {
	          add = false;
	        }
	      }
	      if (add) {
	        localList.add(each);
	      }
	    }
	    return localList.size();
	  }
	}


