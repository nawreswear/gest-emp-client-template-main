package tn.iset.m2glnt.client.viewer.view.dashboard;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tn.iset.m2glnt.client.service.*;
import tn.iset.m2glnt.client.service.dao.*;
import tn.iset.m2glnt.client.viewer.view.LoginView;

public class AdminDashboard {
    private final Stage primaryStage;
    private final BorderPane root;
    private final UserService userService;
    private final AdminService adminService;
    private final EtudiantService etudiantService;
    private final SalleRestService salleService;
    //private final CoursService coursService;

    public AdminDashboard(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new BorderPane();
        this.userService = new UserService();
        this.adminService = new AdminService();
        this.etudiantService = new EtudiantService();
        this.salleService = new SalleRestService();
        //this.coursService = new CoursService();
    }

    public Scene createDashboardScene() {
        // Header
        HBox header = createHeader();

        // Sidebar
        VBox sidebar = createSidebar();

        // Content area
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(content);

        // Afficher la gestion des utilisateurs par défaut
        showUserManagement(content);

        return new Scene(root, 1200, 800);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50;");
        header.setSpacing(20);

        Label title = new Label("Tableau de Bord Administrateur");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Déconnexion");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
           // LoginView loginView = new LoginView(primaryStage);
           // primaryStage.setScene(loginView.createLoginScene());
        });

        header.getChildren().addAll(title, spacer, logoutButton);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(250);

        String buttonStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14; -fx-alignment: center-left; -fx-padding: 12;";
        String buttonHoverStyle = "-fx-background-color: #3498db; -fx-text-fill: white;";

        Button usersBtn = createSidebarButton("👥 Gestion Utilisateurs", buttonStyle, buttonHoverStyle);
        Button adminsBtn = createSidebarButton("🛡️ Gestion Admins", buttonStyle, buttonHoverStyle);
        Button etudiantsBtn = createSidebarButton("🎓 Gestion Étudiants", buttonStyle, buttonHoverStyle);
        Button sallesBtn = createSidebarButton("🏫 Gestion Salles", buttonStyle, buttonHoverStyle);
        Button coursBtn = createSidebarButton("📚 Gestion Cours", buttonStyle, buttonHoverStyle);

        sidebar.getChildren().addAll(usersBtn, adminsBtn, etudiantsBtn, sallesBtn, coursBtn);

        // Gestion des clics
        usersBtn.setOnAction(e -> showUserManagement((StackPane) root.getCenter()));
        adminsBtn.setOnAction(e -> showAdminManagement((StackPane) root.getCenter()));
        etudiantsBtn.setOnAction(e -> showEtudiantManagement((StackPane) root.getCenter()));
        sallesBtn.setOnAction(e -> showSalleManagement((StackPane) root.getCenter()));
        //coursBtn.setOnAction(e -> showCoursManagement((StackPane) root.getCenter()));

        return sidebar;
    }

    private Button createSidebarButton(String text, String normalStyle, String hoverStyle) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));

        return button;
    }

    private void showUserManagement(StackPane content) {
        UserRestService userService = new UserRestService();
        UserManagementView userView = new UserManagementView(userService);
        content.getChildren().setAll(userView.createView());
    }

    private void showAdminManagement(StackPane content) {
        AdminRestService adminService = new AdminRestService();
        AdminManagementView adminView = new AdminManagementView(adminService);
        content.getChildren().setAll(adminView.createView());
    }

    private void showEtudiantManagement(StackPane content) {
        EtudiantRestService etudiantService = new EtudiantRestService();
        EtudiantManagementView etudiantView = new EtudiantManagementView(etudiantService);
        content.getChildren().setAll(etudiantView.createView());
    }

    private void showSalleManagement(StackPane content) {
        SalleRestService salleService = new SalleRestService();
        SalleManagementView salleView = new SalleManagementView(salleService);
        content.getChildren().setAll(salleView.createView());
    }


}