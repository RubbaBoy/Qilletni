package is.yarr.qilletni.database.repositories;

import is.yarr.qilletni.user.SpotifyUserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserInfoRepository extends CrudRepository<SpotifyUserInfo, String> {
}
