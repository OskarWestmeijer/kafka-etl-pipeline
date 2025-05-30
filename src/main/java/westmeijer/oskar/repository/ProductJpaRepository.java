package westmeijer.oskar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import westmeijer.oskar.repository.model.ProductEntity;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {

}
