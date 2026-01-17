package mvc.model.inventory;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mvc.model.entries.Item;

public class InventoryCompositeTest {

    private IInventoryElement rootInv, backpackInv, bigPocketInv, RSmallPocketInv, LSmallPocketInv;
    private Item blade, potion, torch, rope, coin, key, book, shield, gem, dagger, backpack, big_pocket, small_pocket;

    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    public static final String RESET = "\u001B[0m";
    
    @BeforeEach
    public void setUp() {
        blade = new Item("Blade", "A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting", null, 1);
        potion = new Item("Health Potion", "A small vial containing a red liquid that restores health when consumed", null, 2);
        torch = new Item("Torch", "A wooden stick with a flammable tip, used to provide light in dark areas", null, 3);
        rope = new Item("Rope", "A strong, flexible cord used for climbing, tying, or securing objects", null, 4);
        coin = new Item("Gold Coin", "A shiny round coin of gold, used as currency or for trading", null, 5);
        key = new Item("Key", "A small metal object used to unlock a specific lock or door", null, 6);
        book = new Item("Ancient Book", "A dusty, old tome containing mysterious writings and knowledge", null, 7);
        shield = new Item("Wooden Shield", "A sturdy shield made of wood, used to block attacks and protect oneself", null, 8);
        gem = new Item("Ruby Gem", "A precious red gemstone, valuable and often used for crafting or trading", null, 9);
        dagger = new Item("Dagger", "A short, sharp blade designed for quick stabbing attacks", null, 10);
        backpack = new Item("Backpack", "A sturdy bag used to carry items and equipment on your back", null, 11);
        big_pocket = new Item("Big Pocket", "A pocket or pouch with many space", null, 12);
        small_pocket = new Item("Small Pocket", "A pocket or pouch with small space", null, 13);

        rootInv = new InventoryObject(null); 
    }

    @Test
    public void testAddItems() {

        
        rootInv.addItem(this.dagger, 2, false);
        backpackInv = rootInv.addItem(this.backpack, 1, true);
        bigPocketInv = backpackInv.addItem(big_pocket, 1, true);
        RSmallPocketInv = backpackInv.addItem(small_pocket, 1, true);
        LSmallPocketInv = backpackInv.addItem(small_pocket, 1, true);
        
        

        assertEquals(
        CYAN + "Root Inventory" + RESET + "\n" +
        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET
        , rootInv.toString());

        ArrayList<ItemStack> list1 = new ArrayList<>();
        list1.add(new ItemStack(blade, 1));
        list1.add(new ItemStack(rope, 1));
        list1.add(new ItemStack(torch, 1));
        list1.add(new ItemStack(shield, 1));

        backpackInv.addSeveralLeafItems(list1);
        

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET 

         
        , rootInv.toString()); 

        ArrayList<ItemStack> list2 = new ArrayList<>();
        list2.add(new ItemStack(book, 3));
        list2.add(new ItemStack(gem, 2));
        list2.add(new ItemStack(coin, 26));
        list2.add(new ItemStack(rope, 1)); 

        bigPocketInv.addSeveralLeafItems(list2);
        

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +

        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 26" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +

        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET 

         
        , rootInv.toString());

        ArrayList<ItemStack> list3 = new ArrayList<>();
        list3.add(new ItemStack(coin, 56));
        list3.add(new ItemStack(potion, 2));
        list3.add(new ItemStack(key, 2)); 

        RSmallPocketInv.addSeveralLeafItems(list3);
        

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 26" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET 

         
        , rootInv.toString());
            
        rootInv.addItem(coin, 12+1, false);

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET 

         
        , rootInv.toString()); 

    }

    @Test
    public void testModifyAmount(){
        this.testAddItems();
        rootInv.modifyAmount(coin, 14);
        
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 53" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET 
        , rootInv.toString());

        backpackInv.modifyAmount(small_pocket, 2);
        
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 53" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET 
        , rootInv.toString());

        backpackInv.modifyAmount(small_pocket, -3);

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 53" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
 
        
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET 
        , rootInv.toString());
        
        rootInv.modifyAmount(coin, -57);

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" + 

        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 52" + RESET + "\n" +
        "\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET 

         
        , rootInv.toString());

        

        // Testear modificar en negativo quitando x inventario que se contiene a si mismo en x cantidad
        rootInv.find(small_pocket).addItem(small_pocket, 1, true).addItem(small_pocket, 1, true).addItem(small_pocket, 1, true);

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" + 

        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 52" + RESET + "\n" +
        "\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET 

         
        , rootInv.toString());

        rootInv.modifyAmount(small_pocket, -4);

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" + 

        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 52" + RESET + "\n" +
        "\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET 

         
        , rootInv.toString());


        
    }

    @Test
    public void testContains(){
        this.testAddItems();
        assertTrue(rootInv.contains(blade));
        assertTrue(rootInv.contains(potion));
        assertTrue(rootInv.contains(torch));
        assertTrue(rootInv.contains(rope));
        assertTrue(rootInv.contains(coin));
        assertTrue(rootInv.contains(key));
        assertTrue(rootInv.contains(book));
        assertTrue(rootInv.contains(shield));
        assertTrue(rootInv.contains(gem));
        assertTrue(rootInv.contains(dagger));
        assertTrue(rootInv.contains(backpack));
        assertTrue(rootInv.contains(big_pocket));
        assertTrue(rootInv.contains(small_pocket));

        assertFalse(rootInv.contains(new Item("Bread", "A warm piece of bread", null, 100)));
        assertFalse(rootInv.contains(new Item("Bow", "A recurved wooden piece stretched with a string capable of firing arrows", null, 101)));

    }

    @Test
    public void testGetAmmount(){
        this.testAddItems();
        assertEquals(1, rootInv.getAmount(blade));
        assertEquals(2, rootInv.getAmount(potion));
        assertEquals(1, rootInv.getAmount(torch));
        assertEquals(2, rootInv.getAmount(rope));
        assertEquals(95, rootInv.getAmount(coin));
        assertEquals(2, rootInv.getAmount(key));
        assertEquals(3, rootInv.getAmount(book));
        assertEquals(1, rootInv.getAmount(shield));
        assertEquals(2, rootInv.getAmount(gem));
        assertEquals(2, rootInv.getAmount(dagger));
        assertEquals(1, rootInv.getAmount(backpack));
        assertEquals(1, rootInv.getAmount(big_pocket));
        assertEquals(2, rootInv.getAmount(small_pocket));

        assertEquals(1, rootInv.getAmount());
    }
     
    @Test
    public void testDeleteItem(){
        this.testAddItems();
        rootInv.deleteItem(coin);
        assertEquals(0, rootInv.getAmount(coin));
        assertFalse(rootInv.contains(coin));
    }

    @Test
    public void testFindNodes(){
        this.testAddItems();
        ArrayList<IInventoryElement> l1, l2, l3, l4, l5;
        l1 = new ArrayList<IInventoryElement>(rootInv.findNodes(coin));
        l2 = new ArrayList<IInventoryElement>(rootInv.findNodes(small_pocket));
        l3 = new ArrayList<IInventoryElement>(rootInv.findNodes(rope));
        l4 = new ArrayList<IInventoryElement>(rootInv.findNodes(blade));
        l5 = new ArrayList<IInventoryElement>(rootInv.findNodes(potion));

        assertEquals(2, l1.size());
        assertEquals(2, l2.size());
        assertEquals(2, l3.size());
        assertEquals(1, l4.size());
        assertEquals(1, l5.size());

        for (IInventoryElement elem : l1) {
            assertEquals(coin, elem.getItem());
        }
        for (IInventoryElement elem : l2) {
            assertEquals(small_pocket, elem.getItem());
        }
        for (IInventoryElement elem : l3) { 
            assertEquals(rope, elem.getItem());
        }
        for (IInventoryElement elem : l4) {
            assertEquals(blade, elem.getItem());
        }
        for (IInventoryElement elem : l5) {
            assertEquals(potion, elem.getItem());
        }
    }

    @Test 
    public void testItemObject(){
        ItemObject invCoin1 = new ItemObject(coin, 0);
        ItemObject invCoin2 = new ItemObject(coin, -23);
        assertEquals(0, invCoin1.getAmount());
        assertEquals(0, invCoin2.getAmount());

        ItemObject invCoin = new ItemObject(coin, 12);
        assertEquals(12, invCoin.getAmount());

        ArrayList<ItemStack> input = new ArrayList<ItemStack>();
        input.add(new ItemStack(gem, 13));
        input.add(new ItemStack(coin, 12));
        input.add(new ItemStack(backpack, 2));
        input.add(new ItemStack(coin, 4));
        invCoin.addSeveralLeafItems(input);
        invCoin.addItem(backpack, 1, true);
        invCoin.addItem(backpack, -1, true);
        assertEquals(28, invCoin.getAmount());

        invCoin.modifyAmount(gem, 2); 
        invCoin.modifyAmount(gem, 0); 
        assertEquals(28, invCoin.getAmount());

        assertTrue(invCoin.contains(coin));
        assertFalse(invCoin.contains(dagger));

        invCoin.deleteItem(gem);
        assertEquals(28, invCoin.getAmount());

        invCoin.deleteItem(coin);
        assertEquals(0, invCoin.getAmount());

        assertNull(invCoin.findNodes(gem));
    }

    @Test
    public void testAddItemsHere() {
        this.testAddItems();
        
        rootInv.addItemHere(coin, 212, false);
        rootInv.addItemHere(backpack, 2, true);
        rootInv.addItemHere(coin, 12, false);
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 224" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET 
        
         
        , rootInv.toString()); 

        ArrayList<ItemStack> list1 = new ArrayList<>();
        list1.add(new ItemStack(coin, 45));
        list1.add(new ItemStack(gem, 13));
        list1.add(new ItemStack(blade, 1));
        list1.add(new ItemStack(gem, 12));

        rootInv.addSeveralLeafItemsHere(list1);

        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 269" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 25" + RESET + "\n" +
        "\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET
        
         
        , rootInv.toString()); 

    }


    @Test
    public void testModifyAmountHere(){
        this.testAddItems();
        rootInv.modifyAmountHere(coin, 14);
        
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET  
        , rootInv.toString());


        rootInv.addItemHere(coin, 13, false);
        rootInv.modifyAmountHere(coin, 14);
        
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 27" + RESET  
        , rootInv.toString());

        backpackInv.modifyAmountHere(big_pocket, 3);
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Big Pocket: A pocket or pouch with many space. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 27" + RESET 
        
        , rootInv.toString());

        backpackInv.modifyAmountHere(big_pocket, -4);
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" + 
        
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 27" + RESET 
        
        , rootInv.toString());

        backpackInv.modifyAmountHere(small_pocket, 1);
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" + 
        
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 27" + RESET 
        
        , rootInv.toString());

        RSmallPocketInv.addItem(small_pocket, 1, true);
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" + 
        
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 27" + RESET 
        
        , rootInv.toString());

        backpackInv.deleteItemHere(small_pocket);
        assertEquals( 
        CYAN + "Root Inventory" + RESET + "\n" +    

        "\t" + GREEN + "Dagger: A short, sharp blade designed for quick stabbing attacks. Amount 2" + RESET + "\n" +
        "\t" + CYAN + "Backpack: A sturdy bag used to carry items and equipment on your back. Amount 1" + RESET + "\n" + 
        
         
        
         
        "\t\t" + GREEN + "Blade: A sharp, flat cutting edge of a tool or weapon, designed for slicing or cutting. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Torch: A wooden stick with a flammable tip, used to provide light in dark areas. Amount 1" + RESET + "\n" +
        "\t\t" + GREEN + "Wooden Shield: A sturdy shield made of wood, used to block attacks and protect oneself. Amount 1" + RESET + "\n" +
         
        "\t\t" + GREEN + "Ancient Book: A dusty, old tome containing mysterious writings and knowledge. Amount 3" + RESET + "\n" +
        "\t\t" + GREEN + "Ruby Gem: A precious red gemstone, valuable and often used for crafting or trading. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 39" + RESET + "\n" +
        "\t\t" + GREEN + "Rope: A strong, flexible cord used for climbing, tying, or securing objects. Amount 1" + RESET + "\n" +
         
        "\t\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 56" + RESET + "\n" +
        "\t\t" + GREEN + "Health Potion: A small vial containing a red liquid that restores health when consumed. Amount 2" + RESET + "\n" +
        "\t\t" + GREEN + "Key: A small metal object used to unlock a specific lock or door. Amount 2" + RESET + "\n" +
        "\t\t" + CYAN + "Small Pocket: A pocket or pouch with small space. Amount 1" + RESET + "\n" +
        "\t" + GREEN + "Gold Coin: A shiny round coin of gold, used as currency or for trading. Amount 27" + RESET 
        
        , rootInv.toString());

    }

    @Test
    public void testContainsHere(){
        this.testAddItems();
        assertTrue(backpackInv.containsHere(blade));
        assertFalse(backpackInv.containsHere(potion));
        assertTrue(backpackInv.containsHere(torch));
        assertTrue(backpackInv.containsHere(rope));
        assertFalse(backpackInv.containsHere(coin));
        assertFalse(backpackInv.containsHere(key));
        assertFalse(backpackInv.containsHere(book));
        assertTrue(backpackInv.containsHere(shield));
        assertFalse(backpackInv.containsHere(gem));
        assertFalse(backpackInv.containsHere(dagger));
        assertFalse(backpackInv.containsHere(backpack));
        assertTrue(backpackInv.containsHere(big_pocket));
        assertTrue(backpackInv.containsHere(small_pocket));

        assertFalse(backpackInv.containsHere(new Item("Bread", "A warm piece of bread", null, 100)));
        assertFalse(backpackInv.containsHere(new Item("Bow", "A recurved wooden piece stretched with a string capable of firing arrows", null, 101)));

    }

    @Test
    public void testGetAmmountHere(){
        this.testAddItems();
        assertEquals(1, backpackInv.getAmountHere(blade));
        assertEquals(0, backpackInv.getAmountHere(potion));
        assertEquals(1, backpackInv.getAmountHere(torch));
        assertEquals(1, backpackInv.getAmountHere(rope));
        assertEquals(0, backpackInv.getAmountHere(coin));
        assertEquals(0, backpackInv.getAmountHere(key));
        assertEquals(0, backpackInv.getAmountHere(book));
        assertEquals(1, backpackInv.getAmountHere(shield));
        assertEquals(0, backpackInv.getAmountHere(gem));
        assertEquals(0, backpackInv.getAmountHere(dagger));
        assertEquals(0, backpackInv.getAmountHere(backpack));
        assertEquals(1, backpackInv.getAmountHere(big_pocket));
        assertEquals(2, backpackInv.getAmountHere(small_pocket));

        //assertEquals(1, rootInv.getAmount());
    }

    @Test
    public void testFindNodesHere(){
        this.testAddItems();
        ArrayList<IInventoryElement> l1, l2, l3, l4, l5, l6, l7;
        l1 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(big_pocket));
        l2 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(small_pocket));
        l3 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(rope));
        l4 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(blade));
        l5 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(potion));
        l6 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(shield));
        l7 = new ArrayList<IInventoryElement>(backpackInv.findNodesHere(coin)); 

        assertEquals(1, l1.size());
        assertEquals(2, l2.size());
        assertEquals(1, l3.size());
        assertEquals(1, l4.size());
        assertEquals(0, l5.size());
        assertEquals(1, l6.size());
        assertEquals(0, l7.size());

         
    }

    @Test 
    public void testItemObjectHere(){ 
        ItemObject invCoin = new ItemObject(coin, 12);
        assertEquals(12, invCoin.getAmount());

        ArrayList<ItemStack> input = new ArrayList<ItemStack>();
        input.add(new ItemStack(gem, 13));
        input.add(new ItemStack(coin, 12));
        input.add(new ItemStack(backpack, 2));
        input.add(new ItemStack(coin, 4));
        invCoin.addSeveralLeafItemsHere(input);
        invCoin.addItemHere(backpack, 1, true);
        invCoin.addItemHere(backpack, -1, true);
        assertEquals(28, invCoin.getAmount());

        invCoin.modifyAmountHere(gem, 2); 
        invCoin.modifyAmountHere(gem, 0); 
        assertEquals(28, invCoin.getAmount());

        assertTrue(invCoin.containsHere(coin));
        assertFalse(invCoin.containsHere(dagger));

        assertEquals(invCoin, invCoin.findHere(coin));
        invCoin.deleteItemHere(gem);
        assertEquals(28, invCoin.getAmount());
        assertEquals(0, invCoin.getAmountHere(gem));
        invCoin.setAmount(45);
        assertEquals(45, invCoin.getAmountHere(coin));

        invCoin.deleteItemHere(coin);
        assertEquals(0, invCoin.getAmount());

        assertNull(invCoin.findNodesHere(gem));
        assertNull(invCoin.getInventory());
        assertNull(invCoin.addItem(null, 10, false));
        assertEquals(0, invCoin.getAmount());
    }

}
