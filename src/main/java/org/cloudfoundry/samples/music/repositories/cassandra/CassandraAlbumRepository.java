package org.cloudfoundry.samples.music.repositories.cassandra;

import org.cloudfoundry.samples.music.domain.Album;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("cassandra")
public interface CassandraAlbumRepository extends CassandraRepository<Album, String> {
}
