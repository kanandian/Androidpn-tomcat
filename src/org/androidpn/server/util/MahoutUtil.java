package org.androidpn.server.util;

import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.model.User;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.hsqldb.util.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MahoutUtil {

    private static MahoutUtil mahoutUtil = null;
//
    private final static int NEIGHBORHOOD_NUM = 2;
    private final static int RECOMMENDER_NUM = 3;
//
    private BussinessService bussinessService;


    private MahoutUtil() {
        bussinessService = ServiceLocator.getBussinessService();
    }

    public static MahoutUtil getInstance() {
        if (mahoutUtil == null) {
            synchronized (MahoutUtil.class) {
                if (mahoutUtil == null) {
                    mahoutUtil = new MahoutUtil();
                }
            }
        }

        return mahoutUtil;
    }

    public void createNewDataSet() {
        RedisUtil redisUtil = RedisUtil.getInstance();
        File file = new File(Constant.MAHOUT_DATASET_FILE_URL);
        FileWriter writer = null;
        try {
             writer = new FileWriter(file);
             List<Comment> commentList = bussinessService.getAllComments();

//             redisUtil.clearAll();
//             List<Long> userIdList  = new ArrayList<Long>(commentList.size());
//             List<String> userNameList = new ArrayList<String>(commentList.size());

             for (int i=0;i<commentList.size();i++) {
                 Comment comment = commentList.get(i);
//                 if (!userNameList.contains(comment.getUserName())) {
//                     userIdList.add((long) i);
//                     userNameList.add(comment.getUserName());
//                 }

//                 long[] contents = new long[]{comment.getUserId(), comment.getBussiness().getBussinessId(), comment.getStar()};
                 writer.write(comment.getUserId()+","+comment.getBussiness().getBussinessId()+","+comment.getStar()+"\n");
             }

//             redisUtil.set(userIdList, userNameList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void updateDataSet() {

    }

    public List<Bussiness> getPerferencesBussinesses(String userName, int count) {
        List<RecommendedItem> userItems = null;
        List<RecommendedItem> itemItems = null;

        long userId = 0;
        try {
            User user = ServiceLocator.getUserService().getUserByUsername(userName);
            userId = user.getId();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        try {
            userItems = getUserBasedItems(userId, count/2);
            itemItems = getItemBasedItems(userId, count-count/2);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TasteException e) {
            e.printStackTrace();
        }

        List<Long> ids = new ArrayList<Long>();
        if (userItems != null) {
            for (RecommendedItem item : userItems) {
                ids.add(item.getItemID());
            }
        }
        if (itemItems != null) {
            for (RecommendedItem item : itemItems) {
                ids.add(item.getItemID());
            }
        }

        return bussinessService.getBussinessesByIds(ids);

    }

    public List<RecommendedItem> getUserBasedItems(long userId, int count) throws IOException, TasteException {
        List<RecommendedItem> recommendedItems = null;
        try {
            DataModel model = new FileDataModel(new File(Constant.MAHOUT_DATASET_FILE_URL));
            UserSimilarity user = new EuclideanDistanceSimilarity(model);
            NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, user, model);
            Recommender recommender = new GenericUserBasedRecommender(model, neighbor, user);

            recommendedItems = recommender.recommend(userId, count);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        LongPrimitiveIterator iter = model.getUserIDs();
//
//        while (iter.hasNext()) {
//            long uid = iter.nextLong();
//            List<RecommendedItem> list = recommender.recommend(uid, RECOMMENDER_NUM);
//            System.out.printf("uid:%s", uid);
//            for (RecommendedItem ritem : list) {
//                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
//            }
//            System.out.println();
//        }


        return recommendedItems;
    }

    public List<RecommendedItem> getItemBasedItems(long  userId, int count) throws IOException, TasteException {
        DataModel model = new FileDataModel(new File(Constant.MAHOUT_DATASET_FILE_URL));
        ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
        Recommender itemBasedRecommender = new GenericItemBasedRecommender(model, similarity);
        List<RecommendedItem> recommendations = itemBasedRecommender.recommend(userId, count);

        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }

        return recommendations;

    }

}
