from sqlalchemy.orm import Session
from app.database import SessionLocal
import pandas as pd
from mlxtend.frequent_patterns import apriori, association_rules

def get_association_recommendations(user_id: int):
    db: Session = SessionLocal()
    query = """
        SELECT o.user_id, ol.order_id, ol.product_id
        FROM orders o
        JOIN order_lines ol ON o.order_id = ol.order_id
        WHERE o.user_id = :user_id
    """
    df = pd.read_sql(query, db.bind, params={"user_id": user_id})
    db.close()

    if df.empty:
        return {"recommendations": []}

    basket = df.groupby(['order_id', 'product_id'])['product_id'] \
               .count().unstack().fillna(0)
    basket = basket.applymap(lambda x: 1 if x > 0 else 0)

    frequent_items = apriori(basket, min_support=0.01, use_colnames=True)
    rules = association_rules(frequent_items, metric="lift", min_threshold=1.0)

    recommendations = []
    for _, row in rules.iterrows():
        for item in row['consequents']:
            recommendations.append({
                "product_id": item,
                "name": f"Producto {item}",
                "score": round(row['lift'], 2)
            })

    return {"recommendations": recommendations}
