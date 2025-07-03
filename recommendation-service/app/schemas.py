from pydantic import BaseModel
from typing import List
from uuid import UUID

class Recommendation(BaseModel):
    product_id: UUID
    name: str
    score: float

class RecommendationResponse(BaseModel):
    recommendations: List[Recommendation]
