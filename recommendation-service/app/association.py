from sqlalchemy.orm import Session
from app.database import SessionLocal
import pandas as pd
from mlxtend.frequent_patterns import apriori, association_rules
from uuid import UUID
import logging

def get_association_recommendations(user_id: UUID):
    try:
        db: Session = SessionLocal()

        query = """
            SELECT o.user_id, ol.order_id, ol.product_id, p.name
            FROM orders o
            JOIN order_lines ol ON o.order_id = ol.order_id
            JOIN products p ON ol.product_id = p.product_id
            WHERE o.user_id = %(user_id)s
        """
        df = pd.read_sql(query, db.bind, params={"user_id": str(user_id)})
        db.close()

        if df.empty:
            return {"recommendations": []}

        # Crear el mapa de ID a nombre del producto
        id_to_name = df.drop_duplicates(subset=["product_id"]).set_index("product_id")["name"].to_dict()

        # Transformación a formato cesta
        basket = df.groupby(['order_id', 'product_id'])['product_id'] \
            .count().unstack().fillna(0)
        basket = basket.applymap(lambda x: 1 if x > 0 else 0)

        # Reglas de asociación
        frequent_items = apriori(basket, min_support=0.01, use_colnames=True)
        rules = association_rules(frequent_items, metric="lift", min_threshold=1.0)

        recommendations = []
        seen_products = set()

        for _, row in rules.iterrows():
            for item in list(row['consequents']):
                if item not in seen_products:
                    seen_products.add(item)
                    recommendations.append({
                        "product_id": item,
                        "name": id_to_name.get(item, f"Producto {item}"),
                        "score": round(row['lift'], 2)
                    })

        return {"recommendations": recommendations}

    except Exception as e:
        logging.error(f"Error al generar recomendaciones: {e}")
        return {"recommendations": []}
