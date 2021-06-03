package test.com.mattgoodrichapps.userinput;

import com.mattgoodrichapps.userinput.InputParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class InputParserTest {

   InputParser parser;

   @BeforeEach
   public void setup() {
      parser = new InputParser();
   }

   @Test
   public void testParseAirportIdentifiers_commasNoSpaces() {
      assertEquals(parser.parseAirportIdentifiers("c65,ksbn"), Arrays.asList("c65", "ksbn"));
   }
}
