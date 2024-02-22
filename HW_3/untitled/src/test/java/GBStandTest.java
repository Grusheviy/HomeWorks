import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pom.StudentPage;
import pom.ProfilePage;
import java.io.IOException;
import static com.codeborne.selenide.Condition.text;
import static org.junit.jupiter.api.Assertions.*;


public class GBStandTest extends AbstractTest {

    /* 1) Логин в систему не вводя ни логин, ни пароль.
     На странице появляется ошибка, нужно проверить её текст
     */
    @Test
    @Order(1)
    public void loginWithoutUsernamePasswordExpectedErrorTest() throws IOException {
        loginPage.login("", "");

        String errorMessage = loginPage.getErrorText();
        assertTrue(errorMessage.contains("Invalid credentials."));
        takeScreenshot("loginWithoutUsernamePasswordExpectedErrorTest");
    }


/*
2) Более сложный тест на редактирование dummy (здесь будет важно добавить методы для поиска ID dummy по имени,
 чтобы сохранить его в переменную и потом в завершении теста использовать для проверки). Сценарий теста:
- Открываем модальное окно для редактирования dummy
- Редактируем имя
- Сохраняем
- Проверяем в таблице Dummies по ID, что имя изменилось
 */
    @Test
    @Order(2)
    public void findStudentNameByIdAfterRenameTest() throws IOException, InterruptedException {
        loginPage.login(USERNAME, PASSWORD);

        String testStudentName = "NewStudent" + System.currentTimeMillis();
        String testStudentLogin = "NewStudent" + System.currentTimeMillis();
        Selenide.sleep(3000);
        studentPage.createStudent(testStudentName, testStudentLogin);

        studentPage.closeEditStudentModalWindow();

        Selenide.sleep(2000);

        // Получаем исходное имя и ID student
        String originalStudentName = studentPage.getFirstGeneratedStudentName();
        String originalStudentId = studentPage.getStudentRowByName(originalStudentName).getID();


        // Редактируем student
        String newStudentName = "EditedStudent" + System.currentTimeMillis();
        studentPage.editStudent(newStudentName);

        // Получаем отредактированное имя и ID student
        String editedStudentName = studentPage.getFirstGeneratedStudentName();
        String editedStudentId = studentPage.getStudentRowByName(editedStudentName).getID();

        // Проверяем, что имя изменилось
        assertEquals(newStudentName, editedStudentName);

        // Проверяем, что имя в таблице Students по ID изменилось
        studentPage.waitAndGetStudentTitleByText(editedStudentName).shouldHave(text(editedStudentName));

        System.out.println("Original Student Name: " + testStudentName);
        System.out.println("Edited Student Name: " + newStudentName);
        System.out.println("Original Student ID: " + originalStudentId);
        System.out.println("Edited Student ID: " + editedStudentId);

        takeScreenshot("editStudentAndGetNameByID");
    }

/*
3) Тест на проверку модального окна Credentials
- Открыть модальное окно на созданном Dummy
- Проверить заголовок и статический контент
 */
    @Test
    @Order(3)
    public void credentialsModalTest() throws InterruptedException, IOException {
        loginPage.login(USERNAME, PASSWORD);
    //    StudentPage studentPage = Selenide.page(StudentPage.class);

        Selenide.sleep(2000);
        studentCredentialsModal.openCredentialsModal();

        String actualTitle = studentCredentialsModal.getCredentialsModalTitle();
        String actualContent = studentCredentialsModal.getCredentialsModalContent();

        System.out.println("actualTitle " + studentCredentialsModal.getCredentialsModalTitle());
        System.out.println("actualContent " + studentCredentialsModal.getCredentialsModalContent());

        String expectedTitle = "Dummy credentials";
        String expectedContent = studentCredentialsModal.getCredentialsModalContent();

        System.out.println("expectedTitle Dummy credentials" );
        System.out.println("expectedContent " + studentCredentialsModal.getCredentialsModalContent());

        assertNotNull(actualContent);
        assertNotNull(actualTitle);
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedContent, actualContent);


        Thread.sleep(2000);
        takeScreenshot("credentialsModalTest");
        studentCredentialsModal.closeCredentialsModal();
    }
    @Test
    @Order(4)
    void testFullNameProfilePage() throws InterruptedException {
        loginPage.login(USERNAME, PASSWORD);
        StudentPage studentPage = Selenide.page(StudentPage.class);
        ProfilePage profilePage = Selenide.page(ProfilePage.class);

        String labelText = studentPage.getUsernameLabelText();
        assertEquals("Hello, GB202309fef085", labelText);

        assertTrue(studentPage.getUsernameLabelText().contains(USERNAME));

        Selenide.sleep(3000);
        studentPage.clickUsernameLabel();
        Selenide.sleep(3000);
        studentPage.clickProfileLink();

        String actualValueInAdditionalInfo = profilePage.getFullNameInAdditionalInfo();
        System.out.println("Actual value in Additional Info: " + actualValueInAdditionalInfo);
        Selenide.sleep(5000);
        String actualValueInAvatar = profilePage.getFullNameInAvatarSection();
        System.out.println("Actual value in Avatar Section: " + actualValueInAvatar);
        assertEquals("GB202309 fef085", actualValueInAvatar);
        assertEquals("GB202309 fef085", actualValueInAdditionalInfo);
    }
}