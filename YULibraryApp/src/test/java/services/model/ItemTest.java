package model;


import model.assets.Item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {


        private final String title = "title123";
        private final String author = "author123";
        private final String itemId = "123";
        private final boolean enabled = true;
    //public Item item123 = new Item(title,author);  item is substract class

        static class ConcreteItem extends Item {
            ConcreteItem(String title, String author, String itemId, boolean enabled) {
                super(title, author, itemId, enabled);
            }
        }


        @Test
        void testItemConstructorWithSpecificId() {
            Item itemWithId = new ConcreteItem(title, author, itemId, enabled);
            assertEquals(itemId, itemWithId.getId());
            assertEquals(title, itemWithId.getTitle());
            assertEquals(author, itemWithId.getAuthor());
            assertEquals(enabled, itemWithId.isEnabled());
        }

}
