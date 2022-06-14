package fi.muni.courtreservation.court;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author Radim Stejskal 514102
 */
@Service
@AllArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;

    /**
     * Adds new courts to the database
     * @param count Number of Court objects to be saved into the database
     */
    public void addCourts(int count){
        for (int i = 0; i < count; i++){
            Court court = new Court();
            courtRepository.save(court);
        }
    }

    public boolean existsById(Long id){
        return courtRepository.findById(id).isPresent();
    }
    /**
     *
     * @return All Court objects from the database
     */
    public List<Court> getAllCourts(){
        return courtRepository.findAll();
    }

    /**
     * Deletes Court from the database by its id
     * @param id id of the court to be deleted
     */
    public void deleteCourt(long id){
        courtRepository.deleteById(id);
    }
}
