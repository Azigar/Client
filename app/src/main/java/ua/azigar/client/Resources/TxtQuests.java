package ua.azigar.client.Resources;

/**
 * Created by Azigar on 03.09.2015.
 */
public class TxtQuests {

    private static String sex1, sex2, sex3, sex4;

    public String TxtQuests(int id, int isNew, String nameHero, String lvlHero, String sex) {  //главный
        SEX(Integer.parseInt(sex)); //мальчик-девочка
        String txt = "";
        switch (id){

            //Знакомство
            case 1:
                switch (isNew) {
                    case 0: //новый
                        txt = "- О, проснулся! Ты меня понимаешь?" + "\n" +
                                "- Да-а. Кто Вы? Где я?" + "\n" +
                                "- Ты в поселке Фершемпен, а я Староста этого поселения. Мы нашли тебя на равнине после большого взрыва. " +
                                "Думаю ты как-то к этому причастен." + "\n" +
                                "- Я ничего не помню. Не могу вспомнить даже своего имени." + "\n" +
                                "- Вообще я очень удивлён, после таких травм не выживают, а твои раны зажили всего за одну ночь. " +
                                "Мои люди пошли осмотреть место взрыва. " +
                                "Давай подождем когда они вернутся, может они что то узнают. " +
                                "Думаю со временем ты еще многое вспомнишь. " +
                                "Я положил в твой Инвентарь одежду и амулет. Посмотри, оденься." + "\n" +
                                "- На амулете написано " + nameHero + ". Наверное это мое имя." + "\n" +
                                "- Тогда будем называть тебя " + nameHero + ". Когда оденешься приходи ко мне";
                        break;

                    case 1: //прохождение
                        txt = "- Ну давай посмотрю на тебя, подошло ли то что я тебе дал." + "\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "P.S. Если захочешь отказатся от выполненния задания, тогда подойди к тому человеку, " +
                                "у которого надо завершить это задание и нажми \"Отказатся\"";
                        break;

                    case 2: //завершение
                        txt = "- Не плохо на тебе сидит, видишь как я хорошо угадал с размером." + "\n" +
                                "- Когда я одел"+sex1+" амулет, то почуствовал"+sex1+" невероятную силу в себе. " +
                                "Я не могу щас это оьбяснить" + "\n" +
                                " - Думаю, я понимаю о чём ты говоришь... " +
                                "Ладно, пока осмотрись что и как, а потом заходи ко мне." + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "Награда: 50 Опыта";
                        break;

                    case 3: //не прошел проверку
                        txt = "- Тебе так тяжело одется? Давай одевай поскорей все то, что в Инвентаре!" + "\n" +
                                "\n" +
                                "\n" +
                                "\n" +
                                "P.S. Что бы найти \"Инвентарь\" надо нажать на кнопку меню Вашего Андроид-устройства";
                        break;
                }
                break;

            //Надоедливые мухи
            case 2:
                switch (isNew) {
                    case 0: //новый
                        txt = "- Что то долго нет моих ребят." + "\n" +
                                "- Может я схожу, проверю?" + "\n" +
                                "- Неспеши, еще успеешь показать себя. Вот как раз у меня для тебя есть боевое задание." + "\n" +
                                "- Ого! Хорошо, я готов"+sex1+"." + "\n" +
                                "- У меня скоро внук будет спать ложится, а в доме я уже насчитал восемь мух. " +
                                    "Вот твое первое боевое задание - убей этих надоедливых мух! И принеси мне их." + "\n"+
                                "- Хорошо, неволнуйтесь, я с радостью помогу Вам.";
                         break;

                    case 1: //прохождение
                        txt = "- Ну-у... покажи мне восесь мертвых мух";
                        break;

                    case 2: //завершение
                        txt = "- Очень хорошо. Теперь дитя выспится. " +
                                "Кстати крылья мух можеш продать в нашей лавке. Я скажу хозяину лавки, что бы тебя впуспил. " +
                                "Вот возьми эти деньги, может у него еще что прикупишь. И на, держи этот ножик, он лишним не будет." + "\n" +
                                " - Спасибо огромное." +
                                "" + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "Награда: 150 Опыта, 10 монет, Малый нож";
                        break;

                    case 3: //не прошел проверку
                        txt = "- " + nameHero + ", поторопись, пожалуйста, ребёнок скоро будет ложится спать!" + "\n" +
                                "\n" +
                                "\n" +
                                "P.S. Бой происходит в пошаговом режиме. " +
                                    "Нападающий выбирает направление для удара (голова, торс, ноги), а опонент выбирает направление для защиты. " +
                                "Если выбор обоих совпадает, значит нападающий попал в блок. Также возможно пробить блок, если хорошо развита интуиция или " +
                                "увернутся от удара, если развивать ловкость.";
                        break;
                }
                break;

            //Новая сила
            case 3:
                switch (isNew) {
                    case 0: //новый
                        txt = "- Привет " + nameHero + ". Ну как, осмотрелся, ознакомился?" + "\n" +
                                "- Я вновь почуствовал тоже самое." + "\n" +
                                "- Ты о чём это?." + "\n" +
                                "- Туже самую СИЛУ внутри себя. Я уже рассказывал Вам, когда одел свой амулет. " +
                                    "Вы говорили, что знаете, что со мной происходит." + "\n" +
                                "- Дай мне свою руку. А-а-аа, ты получил новый уровень! Твоя сила увеличина. " +
                                "Также ты увеличиваешь свою мощь, одев на себя разные вещи. И еще я чуствую в тебе МАГИЮ. " +
                                "Я сам не маг, потому не могу тебя научить этим пользоватся. Та и рано тебе еще, наберись больше магической силы. " +
                                "А щас проверь свои характеристики. Для этого выбери в меню пункт Персонаж. Попробуй снять с себя вещи, " +
                                "которые я дал тебя, а потом одеть, и посмотри посмотри как это на тебя повлияет.";
                        break;

                    case 1: //прохождение
                        txt = "- Ну как тебе? Зарабатывай больше денег и покупай себе лучшие вещи. " +
                                "Также вещи можна получить при охоте на разных монстров и как награду при выполнении заданий." +
                                "" + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "P.S. Когда есть новое задание на локации, ты увидешь сообщение, а еще возле имени NPS будут знаки восклецания. ";
                        break;

                    case 2: //завершение
                        txt = "- Молодець. " +
                                "" + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "" + "\n" +
                                "Награда: 50 Опыта";
                        break;

                    case 3: //не прошел проверку
                        break;
                }
                break;
        }
        return txt;
    }

    //мальчик-девочка
    private static void SEX(int sex) {
        if (sex == 1){ //если герой - мужик
            sex1 = "";
            sex2 = "я";
            sex3 = "";
            sex4 = "ый";
        }
        else {  //если герой - девушка
            sex1 = "а";
            sex2 = "ь";
            sex3 = "ла";
            sex4 = "ая";
        }
    }
}
