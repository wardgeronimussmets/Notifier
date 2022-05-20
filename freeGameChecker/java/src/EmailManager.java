import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.Buffer;
import java.util.*;

public class EmailManager {
    private Properties prop;
    private Session session;
    private ArrayList<GameDeal> deals;


    public  EmailManager(){
        deals = new ArrayList<>();
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        //prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String[] user = getUser();
                System.out.println(user[0]);
                System.out.println(user[1]);
                return new PasswordAuthentication(user[0],user[1]);
            }
        });


    }

    public void sendMessage(){
        for(String emailAdress: getMailAdresses()) {
            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress("from@gmail.com"));

                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(emailAdress));
                message.setSubject("GameDeals - Hello there, some new deals");

                String msg = generateMailBody();
                System.out.println(msg);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);

                Transport.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getMailAdresses(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("./contacts.txt"));
            Scanner scanner = new Scanner(br);
            ArrayList<String> contacts = new ArrayList<>();
            while(scanner.hasNextLine()){
                contacts.add(scanner.nextLine());
            }
            return contacts;
        }
        catch (Exception e){
            System.out.println("Couldn't read from contacts.txt");
            System.exit(1);
            return null;
        }
    }

    public void appendMessage(HashMap<String,Object> gameInfo){
        deals.add(new GameDeal(gameInfo));
    }
    private String generateMailBody(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The following free games have been found.\n");
        deals.sort(new Comparator<GameDeal>() {
            @Override
            public int compare(GameDeal gameDeal, GameDeal t1) { //negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
                int result = mapCategoryToInt(gameDeal.getCategory()) - mapCategoryToInt(t1.getCategory());
                if(result == 0){
                    return (gameDeal.getCategory().compareToIgnoreCase(t1.getCategory()));
                }
                else return result;
            }
        });
        String prevCat = "";
        for(GameDeal deal: deals){
            if(!prevCat.equals(deal.getCategory())){
                prevCat = deal.getCategory();
                stringBuilder.append("\n\n" + deal.getCategory() + "\n\n");
            }
            stringBuilder.append(deal.getBody()+"\n"+deal.getLink()+"\n");
        }
        stringBuilder.append("\n\n See you next time");
        return stringBuilder.toString();
    }
    private int mapCategoryToInt(String category){
        if(category.equals("Steam")) return 0;
        else if(category.equals("Epic Games")) return 1;
        else return 2;
    }

    private String[] getUser(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("./email.txt"));
            Scanner scanner = new Scanner(br);
            String userName = scanner.nextLine();
            String userPw = scanner.nextLine();
            return new String[]{userName,userPw};
            }
        catch (Exception e){
            System.out.println("Couldn't find emails.txt");
            System.exit(1);
            return null;
        }
    }
}
