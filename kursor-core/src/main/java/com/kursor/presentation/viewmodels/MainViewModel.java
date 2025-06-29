package com.kursor.presentation.viewmodels;

import com.kursor.yaml.dto.CursoDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Modelo de vista para la interfaz principal de Kursor.
 * Maneja los datos observables que se muestran en la UI.
 */
public class MainViewModel {
    
    private final ObservableList<CursoDTO> cursos;
    private final ObjectProperty<CursoDTO> selectedCourse;
    
    public MainViewModel() {
        this.cursos = FXCollections.observableArrayList();
        this.selectedCourse = new SimpleObjectProperty<>();
    }
    
    /**
     * Establece la lista de cursos
     */
    public void setCursos(List<CursoDTO> cursos) {
        this.cursos.clear();
        if (cursos != null) {
            this.cursos.addAll(cursos);
        }
    }
    
    /**
     * Obtiene la lista observable de cursos
     */
    public ObservableList<CursoDTO> getCursos() {
        return cursos;
    }
    
    /**
     * Establece el curso seleccionado
     */
    public void setSelectedCourse(CursoDTO curso) {
        this.selectedCourse.set(curso);
    }
    
    /**
     * Obtiene el curso seleccionado
     */
    public CursoDTO getSelectedCourse() {
        return selectedCourse.get();
    }
    
    /**
     * Obtiene la propiedad observable del curso seleccionado
     */
    public ObjectProperty<CursoDTO> selectedCourseProperty() {
        return selectedCourse;
    }
} 
