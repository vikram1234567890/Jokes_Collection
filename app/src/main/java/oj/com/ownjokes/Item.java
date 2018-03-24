package oj.com.ownjokes;

class Item
{
    public String joke_id,joke, no_of_likes,  no_of_dislikes,like_img,dislike_img;

    public Item(String joke_id,String joke,String like_img,String no_of_likes,String dislike_img,String no_of_dislikes)
    {
        this.joke_id=joke_id;
        this.joke=joke;
        this.like_img=like_img;
        this.no_of_likes=no_of_likes;
        this.dislike_img=dislike_img;
        this.no_of_dislikes=no_of_dislikes;

    }


}