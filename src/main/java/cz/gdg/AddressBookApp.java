package cz.gdg;

import cz.gdg.hk.AddressBookProto.AddressBook;
import cz.gdg.hk.AddressBookProto.Person;

import java.io.*;

public class AddressBookApp {
    private static final String DATA_FILE = "addressbook.dat";
    private AddressBook.Builder addressBook;

    public AddressBookApp() throws IOException {
        addressBook = AddressBook.newBuilder();

        loadAddressBook();

        listPersons();
        readAllPerson();
        listPersons();

        saveAddressBook();
    }

    private void saveAddressBook() throws IOException {
        addressBook.build().writeTo(new FileOutputStream(DATA_FILE));
    }

    private void readAllPerson() throws IOException {
        boolean addAnother = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (addAnother) {
            System.out.println("Add another person to address book?");
            String answer = reader.readLine();
            if (answer.equalsIgnoreCase("yes")) {
                addressBook.addPerson(readPerson(reader));
            } else {
                addAnother = false;
            }
        }
    }

    private Person readPerson(BufferedReader reader) throws IOException {
        Person.Builder person = Person.newBuilder();

        System.out.print("Enter person ID:");
        person.setId(Integer.valueOf(reader.readLine()));

        System.out.print("Enter person name:");
        person.setName(reader.readLine());

        System.out.print("Enter person email (left blank for none):");
        String email = reader.readLine();
        if (!email.isEmpty())
            person.setEmail(email);

        while (true) {
            System.out.print("Enter person phone number (left blank for none):");
            String number = reader.readLine();
            if (!number.isEmpty()) {
                Person.PhoneNumber.Builder phoneNumber = Person.PhoneNumber.newBuilder().setNumber(number);
                System.out.print("Is it (m)obile, (h)ome or (w)ork number?");
                String type = reader.readLine();
                if (type.contains("m"))
                    phoneNumber.setType(Person.PhoneType.MOBILE);
                else if (type.contains("h"))
                    phoneNumber.setType(Person.PhoneType.HOME);
                else phoneNumber.setType(Person.PhoneType.WORK);

                person.addPhone(phoneNumber);
            } else {
                break;
            }
        }

        return person.build();
    }

    private void listPersons() {
        System.out.println("------ ADDRESS BOOK ------");
        for (Person person : addressBook.getPersonList()) {
            System.out.println("**PERSON**");
            System.out.println("Name: \t\t" + person.getName());
            System.out.println("ID: \t\t" + person.getId());
            if (person.hasEmail())
                System.out.println("Email: \t" + person.getEmail());

            for (Person.PhoneNumber phoneNumber : person.getPhoneList()) {
                switch(phoneNumber.getType()) {
                    case MOBILE:
                        System.out.print("Mobile: ");
                        break;
                    case WORK:
                        System.out.print("Work: ");
                        break;
                    case HOME:
                        System.out.print("Home: ");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }
        }
    }

    private void loadAddressBook() {
        try {
            addressBook.mergeFrom(new FileInputStream(DATA_FILE));
            System.out.println(addressBook.build().toString());
        } catch (IOException e) {
            // no way!
        }
    }

    public static void main(String[] args) {
        try {
            new AddressBookApp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
