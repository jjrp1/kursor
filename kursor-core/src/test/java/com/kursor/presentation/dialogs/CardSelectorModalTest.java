package com.kursor.presentation.dialogs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Test para verificar el funcionamiento del CardSelectorModal.
 */
public class CardSelectorModalTest {
    
    private List<SelectableItem> testItems;
    
    @BeforeEach
    void setUp() {
        // Crear elementos de prueba
        testItems = Arrays.asList(
            new TestSelectableItem("1", "Elemento 1", "Descripción 1", "🎯", "#ff6b6b"),
            new TestSelectableItem("2", "Elemento 2", "Descripción 2", "📚", "#4ecdc4"),
            new TestSelectableItem("3", "Elemento 3", "Descripción 3", "⚡", "#45b7d1")
        );
    }
    
    @Test
    void testCardSelectorModalCreation() {
        // Verificar que se puede crear el modal sin errores
        assertDoesNotThrow(() -> {
            CardSelectorModal modal = new CardSelectorModal(
                "Test Modal",
                "Descripción de prueba",
                testItems
            );
            
            // Verificar que el modal se creó correctamente
            assertNotNull(modal);
        });
    }
    
    @Test
    void testCardSelectorModalWithEmptyItems() {
        // Verificar que se puede crear el modal con lista vacía
        assertDoesNotThrow(() -> {
            CardSelectorModal modal = new CardSelectorModal(
                "Test Modal",
                "Descripción de prueba",
                Arrays.asList()
            );
            
            assertNotNull(modal);
        });
    }
    
    @Test
    void testCardSelectorModalWithNullItems() {
        // Verificar que se puede crear el modal con lista null
        assertDoesNotThrow(() -> {
            CardSelectorModal modal = new CardSelectorModal(
                "Test Modal",
                "Descripción de prueba",
                null
            );
            
            assertNotNull(modal);
        });
    }
    
    /**
     * Clase de prueba que implementa SelectableItem.
     */
    private static class TestSelectableItem implements SelectableItem {
        private final String id;
        private final String title;
        private final String description;
        private final String icon;
        private final String color;
        
        public TestSelectableItem(String id, String title, String description, String icon, String color) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.color = color;
        }
        
        @Override
        public String getId() {
            return id;
        }
        
        @Override
        public String getTitle() {
            return title;
        }
        
        @Override
        public String getDescription() {
            return description;
        }
        
        @Override
        public String getIcon() {
            return icon;
        }
        
        @Override
        public String getColor() {
            return color;
        }
    }
} 